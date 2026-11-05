package cat.udl.eps.softarch.demo.handler;

import cat.udl.eps.softarch.demo.domain.AnswerAttempt;
import cat.udl.eps.softarch.demo.domain.AnswerOption;
import cat.udl.eps.softarch.demo.domain.Question;
import cat.udl.eps.softarch.demo.domain.TestAttempt;
import cat.udl.eps.softarch.demo.domain.User;
import cat.udl.eps.softarch.demo.repository.AnswerAttemptRepository;
import org.springframework.data.rest.core.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

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
        User user = CurrentUser.get();
        TestAttempt attempt = answer.getAttempt();
        if (attempt == null || answer.getQuestion() == null) {
            return; // Rejected by @NotNull validation
        }
        // The attempt is an existing entity referenced by the request, not modified by it
        if (!user.getUsername().equals(CurrentUser.usernameOf(attempt.getUser()))) {
            throw new AccessDeniedException("Only the user taking the test can answer it");
        }
        requireInProgress(attempt.getStatus());
        checkConsistency(answer);
        answer.setAnsweredAt(Instant.now());
    }

    @HandleBeforeSave
    public void handleAnswerAttemptPreSave(AnswerAttempt answer) {
        User user = CurrentUser.get();

        // The incoming entity is already merged with the request body: compare with the stored row
        AnswerAttemptRepository.AnswerSnapshot stored = answerAttemptRepository.findSnapshotById(answer.getId())
                .orElseThrow(() -> new AccessDeniedException("Answer not found"));

        if (!user.getUsername().equals(stored.getOwnerUsername())) {
            throw new AccessDeniedException("Only the user taking the test can answer it");
        }
        requireInProgress(stored.getAttemptStatus());
        if (answer.getAttempt() == null || answer.getQuestion() == null
                || !stored.getAttemptId().equals(answer.getAttempt().getId())
                || !stored.getQuestionId().equals(answer.getQuestion().getId())) {
            throw new AccessDeniedException("The attempt and the question of an answer cannot be changed");
        }
        checkConsistency(answer);
        answer.setAnsweredAt(Instant.now());
    }

    private void requireInProgress(TestAttempt.Status status) {
        if (status != TestAttempt.Status.IN_PROGRESS) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "The attempt is already finished");
        }
    }

    // question must belong to the attempted test, and the option to that question
    private void checkConsistency(AnswerAttempt answer) {
        Question question = answer.getQuestion();
        TestAttempt attempt = answer.getAttempt();
        if (question.getTest() == null || attempt.getTest() == null
                || !question.getTest().getId().equals(attempt.getTest().getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The question does not belong to the attempted test");
        }
        AnswerOption selected = answer.getSelectedOption();
        if (selected != null
                && (selected.getQuestion() == null || !selected.getQuestion().getId().equals(question.getId()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The selected option does not belong to the question");
        }
    }
}
