package cat.udl.eps.softarch.demo.handler;

import cat.udl.eps.softarch.demo.domain.Question;
import cat.udl.eps.softarch.demo.domain.Test;
import cat.udl.eps.softarch.demo.domain.User;
import cat.udl.eps.softarch.demo.repository.QuestionRepository;
import cat.udl.eps.softarch.demo.repository.TestRepository;
import cat.udl.eps.softarch.demo.repository.AnswerOptionRepository;
import cat.udl.eps.softarch.demo.repository.AnswerAttemptRepository;
import cat.udl.eps.softarch.demo.repository.ReportRepository;
import cat.udl.eps.softarch.demo.service.TestContentService;
import org.springframework.data.rest.core.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
@RepositoryEventHandler
public class QuestionEventHandler {
    final QuestionRepository questionRepository;
    final TestContentService testContentService;
    final TestRepository testRepository;
    final AnswerOptionRepository answerOptions;
    final AnswerAttemptRepository answers;
    final ReportRepository reports;
    final JdbcTemplate jdbc;

    public QuestionEventHandler(QuestionRepository questionRepository, TestContentService testContentService,
                                TestRepository testRepository, AnswerOptionRepository answerOptions,
                                AnswerAttemptRepository answers, ReportRepository reports,
                                JdbcTemplate jdbc) {
        this.jdbc = jdbc;
        this.questionRepository = questionRepository;
        this.testContentService = testContentService;
        this.testRepository = testRepository;
        this.answerOptions = answerOptions;
        this.answers = answers;
        this.reports = reports;
    }

    @HandleBeforeCreate
    public void handleQuestionPreCreate(Question question) {
        // The test is an existing entity referenced by the request, not modified by it
        checkTargetTest(question, CurrentUser.get());
        if (question.getTest() != null) {
            lockTest(question);
            Long testId = question.getTest().getId();
            int lastIndex = questionRepository.maxOrderIndex(testId);
            for (Question existing : questionRepository.listByTestId(testId)) {
                if (existing.getOrderIndex() == null) {
                    existing.setOrderIndex(++lastIndex);
                    questionRepository.save(existing);
                }
            }
            question.setOrderIndex(lastIndex + 1);
        }
    }

    @HandleBeforeSave
    public void handleQuestionPreSave(Question question) {
        User user = CurrentUser.get();

        // The incoming entity is already merged with the request body: the current owner is
        // read from the database, and the (possibly new) test is checked as well.
        QuestionRepository.QuestionSnapshot stored = questionRepository.findSnapshotById(question.getId())
                .orElseThrow(() -> new AccessDeniedException("Question not found"));

        if (!CurrentUser.isAdmin(user) && !user.getUsername().equals(stored.getTestAuthorUsername())) {
            throw new AccessDeniedException("Only the test author can manage its questions");
        }
        // Answers and the maximum score of the original test depend on it
        if (question.getTest() == null || !stored.getTestId().equals(question.getTest().getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The test of a question cannot be changed");
        }
        checkTargetTest(question, user);
        lockTest(question);
    }

    @HandleBeforeDelete
    public void handleQuestionPreDelete(Question question) {
        checkTargetTest(question, CurrentUser.get());
        lockTest(question);
        reports.setTestForQuestion(question.getId(), question.getTest());
        reports.detachQuestion(question.getId());
        jdbc.update("UPDATE attempt_question_snapshot SET selected_option_id = " +
                "(SELECT a.selected_option_id FROM answer_attempt a WHERE a.attempt_id = attempt_question_snapshot.attempt_id " +
                "AND a.question_id = attempt_question_snapshot.question_id) " +
                "WHERE question_id = ? AND EXISTS (SELECT 1 FROM answer_attempt a " +
                "WHERE a.attempt_id = attempt_question_snapshot.attempt_id AND a.question_id = attempt_question_snapshot.question_id AND a.selected_option_id IS NOT NULL)", question.getId());
        answers.deleteForQuestion(question.getId());
        answerOptions.deleteForQuestion(question.getId());
    }

    private void lockTest(Question question) {
        testRepository.lockForContentChange(question.getTest().getId()).orElseThrow();
    }

    // TEST.max_score is derived from the question scores (2.4.3)
    @HandleAfterCreate
    public void handleQuestionPostCreate(Question question) {
        recalculateMaxScore(question);
    }

    @HandleAfterSave
    public void handleQuestionPostSave(Question question) {
        recalculateMaxScore(question);
    }

    @HandleAfterDelete
    public void handleQuestionPostDelete(Question question) {
        recalculateMaxScore(question);
    }

    private void recalculateMaxScore(Question question) {
        if (question.getTest() != null) {
            Long testId = question.getTest().getId();
            testContentService.recalculateMaxScore(testId);
        }
    }

    private void checkTargetTest(Question question, User user) {
        if (question.getTest() == null) {
            return; // Rejected by @NotNull validation
        }
        if (!CurrentUser.isAdmin(user)
                && (question.getTest().getTestType() == Test.TestType.OFFICIAL
                    || !user.getUsername().equals(CurrentUser.usernameOf(question.getTest().getAuthor())))) {
            throw new AccessDeniedException("Only the test author can manage its questions");
        }
    }
}
