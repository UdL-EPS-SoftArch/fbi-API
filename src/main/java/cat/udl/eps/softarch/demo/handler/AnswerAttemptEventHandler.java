package cat.udl.eps.softarch.demo.handler;

import cat.udl.eps.softarch.demo.domain.AnswerAttempt;
import cat.udl.eps.softarch.demo.domain.User;
import cat.udl.eps.softarch.demo.repository.AnswerAttemptRepository;
import org.springframework.data.rest.core.annotation.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RepositoryEventHandler
public class AnswerAttemptEventHandler {
    final AnswerAttemptRepository answerAttemptRepository;

    public AnswerAttemptEventHandler(AnswerAttemptRepository answerAttemptRepository) {
        this.answerAttemptRepository = answerAttemptRepository;
    }

    @HandleBeforeCreate
    public void handleAnswerAttemptPreCreate(AnswerAttempt answer) {
        checkAttemptOwner(answer);
        answer.setAnsweredAt(Instant.now());
    }

    @HandleBeforeSave
    public void handleAnswerAttemptPreSave(AnswerAttempt answer) {
        checkAttemptOwner(answer);
        answer.setAnsweredAt(Instant.now());
    }

    private void checkAttemptOwner(AnswerAttempt answer) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (answer.getAttempt() == null) return; // Rejected by @NotNull validation
        if (!user.getUsername().equals(answer.getAttempt().getUser().getUsername())) {
            throw new AccessDeniedException("Only the user taking the test can answer it");
        }
    }
}
