package cat.udl.eps.softarch.demo.handler;

import cat.udl.eps.softarch.demo.domain.Question;
import cat.udl.eps.softarch.demo.domain.User;
import cat.udl.eps.softarch.demo.repository.QuestionRepository;
import org.springframework.data.rest.core.annotation.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Component
@RepositoryEventHandler
public class QuestionEventHandler {
    final QuestionRepository questionRepository;

    public QuestionEventHandler(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
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
        checkTargetTest(question, user);
    }

    @HandleBeforeDelete
    public void handleQuestionPreDelete(Question question) {
        checkTargetTest(question, CurrentUser.get());
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
