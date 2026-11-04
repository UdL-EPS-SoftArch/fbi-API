package cat.udl.eps.softarch.demo.handler;

import cat.udl.eps.softarch.demo.domain.Question;
import cat.udl.eps.softarch.demo.domain.User;
import cat.udl.eps.softarch.demo.repository.QuestionRepository;
import org.springframework.data.rest.core.annotation.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
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
        checkTestAuthorOrAdmin(question);
    }

    @HandleBeforeSave
    public void handleQuestionPreSave(Question question) {
        checkTestAuthorOrAdmin(question);
    }

    @HandleBeforeDelete
    public void handleQuestionPreDelete(Question question) {
        checkTestAuthorOrAdmin(question);
    }

    private void checkTestAuthorOrAdmin(Question question) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (question.getTest() == null) return; // Rejected by @NotNull validation
        if (user.getRole() != User.Role.ADMIN
                && !user.getUsername().equals(question.getTest().getAuthor().getUsername())) {
            throw new AccessDeniedException("Only the test author can manage its questions");
        }
    }
}
