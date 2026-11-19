package cat.udl.eps.softarch.demo.handler;

import cat.udl.eps.softarch.demo.domain.AnswerAttempt;
import cat.udl.eps.softarch.demo.domain.AnswerOption;
import cat.udl.eps.softarch.demo.domain.Question;
import cat.udl.eps.softarch.demo.domain.TestAttempt;
import cat.udl.eps.softarch.demo.domain.User;
import cat.udl.eps.softarch.demo.repository.AnswerAttemptRepository;
import cat.udl.eps.softarch.demo.repository.TestRepository;
import cat.udl.eps.softarch.demo.repository.TestAttemptRepository;
import org.springframework.jdbc.core.JdbcTemplate;
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
    final TestRepository tests;
    final TestAttemptRepository attempts;
    final JdbcTemplate jdbc;

    public AnswerAttemptEventHandler(AnswerAttemptRepository answerAttemptRepository, TestRepository tests, TestAttemptRepository attempts, JdbcTemplate jdbc) {
        this.tests = tests;
        this.attempts = attempts;
        this.jdbc = jdbc;
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
        tests.lockForContentChange(attempt.getTest().getId()).orElseThrow();
        requireInProgress(attempts.findSnapshotById(attempt.getId()).orElseThrow().getStatus());
        checkConsistency(answer);
        recordSelection(answer, answer.getSelectedOption());
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
        if (answer.getAttempt() == null || answer.getQuestion() == null
                || !stored.getAttemptId().equals(answer.getAttempt().getId())
                || !stored.getQuestionId().equals(answer.getQuestion().getId())) {
            throw new AccessDeniedException("The attempt and the question of an answer cannot be changed");
        }
        tests.lockForContentChange(answer.getAttempt().getTest().getId()).orElseThrow();
        requireInProgress(attempts.findSnapshotById(stored.getAttemptId()).orElseThrow().getStatus());
        checkConsistency(answer);
        recordSelection(answer, answer.getSelectedOption());
        answer.setAnsweredAt(Instant.now());
    }

    @HandleBeforeLinkSave
    @HandleBeforeLinkDelete
    public void handleAnswerAttemptPreLinkChange(AnswerAttempt answer, Object linked) {
        CurrentUser.denyRelationChange();
    }

    // An answer can only be cleared by its owner while the attempt is in progress:
    // the answers of a submitted attempt are part of an immutable result (2.4.3)
    @HandleBeforeDelete
    public void handleAnswerAttemptPreDelete(AnswerAttempt answer) {
        User user = CurrentUser.get();
        AnswerAttemptRepository.AnswerSnapshot stored = answerAttemptRepository.findSnapshotById(answer.getId())
                .orElseThrow(() -> new AccessDeniedException("Answer not found"));
        if (!user.getUsername().equals(stored.getOwnerUsername())) {
            throw new AccessDeniedException("Only the user taking the test can delete its answers");
        }
        tests.lockForContentChange(answer.getAttempt().getTest().getId()).orElseThrow();
        requireInProgress(attempts.findSnapshotById(stored.getAttemptId()).orElseThrow().getStatus());
        recordSelection(answer, null);
    }

    private void recordSelection(AnswerAttempt answer, AnswerOption selected) {
        jdbc.update("UPDATE attempt_question_snapshot SET selected_option_id = ? WHERE attempt_id = ? AND question_id = ?",
                selected == null ? null : selected.getId(), answer.getAttempt().getId(), answer.getQuestion().getId());
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
        if (Boolean.TRUE.equals(attempt.getGradingSnapshotRecorded()) && attempt.getGradingQuestions().stream()
                .noneMatch(snapshot -> snapshot.getQuestionId().equals(question.getId()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Question was not part of the attempt");
        }
        AnswerOption selected = answer.getSelectedOption();
        if (selected != null
                && (selected.getQuestion() == null || !selected.getQuestion().getId().equals(question.getId()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The selected option does not belong to the question");
        }
    }
}
