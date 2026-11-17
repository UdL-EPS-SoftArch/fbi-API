package cat.udl.eps.softarch.demo.handler;

import cat.udl.eps.softarch.demo.domain.Question;
import cat.udl.eps.softarch.demo.domain.User;
import cat.udl.eps.softarch.demo.repository.QuestionRepository;
import cat.udl.eps.softarch.demo.service.TestContentService;
import org.springframework.data.rest.core.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
@RepositoryEventHandler
public class QuestionEventHandler {
    final QuestionRepository questionRepository;
    final TestContentService testContentService;

    public QuestionEventHandler(QuestionRepository questionRepository, TestContentService testContentService) {
        this.questionRepository = questionRepository;
        this.testContentService = testContentService;
    }

    @HandleBeforeCreate
    public void handleQuestionPreCreate(Question question) {
        // The test is an existing entity referenced by the request, not modified by it
        checkTargetTest(question, CurrentUser.get());
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
    }

    @HandleBeforeDelete
    public void handleQuestionPreDelete(Question question) {
        checkTargetTest(question, CurrentUser.get());
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
            testContentService.recalculateMaxScore(question.getTest().getId());
        }
    }

    private void checkTargetTest(Question question, User user) {
        if (question.getTest() == null) {
            return; // Rejected by @NotNull validation
        }
        if (!CurrentUser.isAdmin(user)
                && !user.getUsername().equals(CurrentUser.usernameOf(question.getTest().getAuthor()))) {
            throw new AccessDeniedException("Only the test author can manage its questions");
        }
    }
}
