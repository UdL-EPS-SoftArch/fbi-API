package cat.udl.eps.softarch.demo.handler;

import cat.udl.eps.softarch.demo.domain.AnswerOption;
import cat.udl.eps.softarch.demo.domain.Question;
import cat.udl.eps.softarch.demo.domain.User;
import cat.udl.eps.softarch.demo.repository.AnswerOptionRepository;
import cat.udl.eps.softarch.demo.repository.TestRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.data.rest.core.annotation.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Component
@RepositoryEventHandler
public class AnswerOptionEventHandler {
    final AnswerOptionRepository answerOptionRepository;
    final TestRepository tests;
    final JdbcTemplate jdbc;

    public AnswerOptionEventHandler(AnswerOptionRepository answerOptionRepository, TestRepository tests, JdbcTemplate jdbc) {
        this.tests = tests;
        this.jdbc = jdbc;
        this.answerOptionRepository = answerOptionRepository;
    }

    @HandleBeforeCreate
    public void handleAnswerOptionPreCreate(AnswerOption option) {
        // The question is an existing entity referenced by the request, not modified by it
        checkTargetQuestion(option.getQuestion(), CurrentUser.get());
        lockTest(option.getQuestion());
    }

    @HandleBeforeSave
    public void handleAnswerOptionPreSave(AnswerOption option) {
        User user = CurrentUser.get();

        // The incoming entity is already merged with the request body: the current owner is
        // read from the database, and the (possibly new) question is checked as well.
        AnswerOptionRepository.OptionSnapshot stored = answerOptionRepository.findSnapshotById(option.getId())
                .orElseThrow(() -> new AccessDeniedException("Answer option not found"));

        if (!CurrentUser.isAdmin(user) && !user.getUsername().equals(stored.getTestAuthorUsername())) {
            throw new AccessDeniedException("Only the test author can manage its answer options");
        }
        checkTargetQuestion(option.getQuestion(), user);
        lockTest(option.getQuestion());
    }

    // A question has a single correct answer: marking an option unmarks the previous one.
    // It runs after saving because a new option has no id before (QuestionTransactionFilter
    // keeps both writes in the request transaction).
    @HandleAfterCreate
    public void handleAnswerOptionPostCreate(AnswerOption option) {
        unmarkOtherCorrectOptions(option);
    }

    @HandleAfterSave
    public void handleAnswerOptionPostSave(AnswerOption option) {
        unmarkOtherCorrectOptions(option);
    }

    private void unmarkOtherCorrectOptions(AnswerOption option) {
        if (Boolean.TRUE.equals(option.getIsCorrect()) && option.getQuestion() != null) {
            answerOptionRepository.unmarkOtherCorrectOptions(option.getQuestion().getId(), option.getId());
        }
    }

    @HandleBeforeDelete
    public void handleAnswerOptionPreDelete(AnswerOption option) {
        checkTargetQuestion(option.getQuestion(), CurrentUser.get());
        lockTest(option.getQuestion());
        jdbc.update("UPDATE attempt_question_snapshot SET selected_option_id = ? WHERE question_id = ? " +
                "AND attempt_id IN (SELECT attempt_id FROM answer_attempt WHERE selected_option_id = ?)",
                option.getId(), option.getQuestion().getId(), option.getId());
        jdbc.update("UPDATE answer_attempt SET selected_option_id = NULL WHERE selected_option_id = ?", option.getId());
    }

    private void lockTest(Question question) {
        if (question != null && question.getTest() != null) {
            tests.lockForContentChange(question.getTest().getId()).orElseThrow();
        }
    }

    private void checkTargetQuestion(Question question, User user) {
        if (question == null) {
            return; // Rejected by @NotNull validation
        }
        String authorUsername = question.getTest() == null ? null : CurrentUser.usernameOf(question.getTest().getAuthor());
        if (!CurrentUser.isAdmin(user) && !user.getUsername().equals(authorUsername)) {
            throw new AccessDeniedException("Only the test author can manage its answer options");
        }
    }
}
