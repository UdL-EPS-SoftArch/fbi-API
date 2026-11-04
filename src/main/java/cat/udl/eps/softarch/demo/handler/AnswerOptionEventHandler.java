package cat.udl.eps.softarch.demo.handler;

import cat.udl.eps.softarch.demo.domain.AnswerOption;
import cat.udl.eps.softarch.demo.domain.User;
import cat.udl.eps.softarch.demo.repository.AnswerOptionRepository;
import org.springframework.data.rest.core.annotation.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RepositoryEventHandler
public class AnswerOptionEventHandler {
    final AnswerOptionRepository answerOptionRepository;

    public AnswerOptionEventHandler(AnswerOptionRepository answerOptionRepository) {
        this.answerOptionRepository = answerOptionRepository;
    }

    @HandleBeforeCreate
    public void handleAnswerOptionPreCreate(AnswerOption option) {
        checkTestAuthorOrAdmin(option);
    }

    @HandleBeforeSave
    public void handleAnswerOptionPreSave(AnswerOption option) {
        checkTestAuthorOrAdmin(option);
    }

    @HandleBeforeDelete
    public void handleAnswerOptionPreDelete(AnswerOption option) {
        checkTestAuthorOrAdmin(option);
    }

    private void checkTestAuthorOrAdmin(AnswerOption option) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (option.getQuestion() == null) return; // Rejected by @NotNull validation
        if (user.getRole() != User.Role.ADMIN
                && !user.getUsername().equals(option.getQuestion().getTest().getAuthor().getUsername())) {
            throw new AccessDeniedException("Only the test author can manage its answer options");
        }
    }
}
