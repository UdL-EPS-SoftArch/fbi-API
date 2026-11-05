package cat.udl.eps.softarch.demo.handler;

import cat.udl.eps.softarch.demo.domain.Test;
import cat.udl.eps.softarch.demo.domain.TestAttempt;
import cat.udl.eps.softarch.demo.domain.User;
import cat.udl.eps.softarch.demo.repository.TestAttemptRepository;
import cat.udl.eps.softarch.demo.service.BadgeService;
import cat.udl.eps.softarch.demo.service.LeaderboardService;
import cat.udl.eps.softarch.demo.service.TestAttemptService;
import org.springframework.data.rest.core.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

@Component
@RepositoryEventHandler
public class TestAttemptEventHandler {
    final TestAttemptRepository testAttemptRepository;
    final TestAttemptService testAttemptService;
    final LeaderboardService leaderboardService;
    final BadgeService badgeService;

    public TestAttemptEventHandler(TestAttemptRepository testAttemptRepository,
                                   TestAttemptService testAttemptService,
                                   LeaderboardService leaderboardService,
                                   BadgeService badgeService) {
        this.testAttemptRepository = testAttemptRepository;
        this.testAttemptService = testAttemptService;
        this.leaderboardService = leaderboardService;
        this.badgeService = badgeService;
    }

    @HandleBeforeCreate
    public void handleTestAttemptPreCreate(TestAttempt attempt) {
        User user = CurrentUser.get();
        boolean admin = CurrentUser.isAdmin(user);

        attempt.setUser(user);
        attempt.setStatus(TestAttempt.Status.IN_PROGRESS);
        attempt.setStartedAt(Instant.now());
        attempt.setCountsForLeaderboard(false);
        // The client cannot choose the type: administrators validate, students take regular attempts
        attempt.setAttemptType(admin ? TestAttempt.AttemptType.VALIDATION : TestAttempt.AttemptType.REGULAR);

        Test test = attempt.getTest();
        if (test == null) {
            return; // Rejected by @NotNull validation
        }
        if (!admin) {
            checkAvailable(test, user);
        }
        attempt.setAttemptNumber((int) testAttemptRepository.countByUserAndTest(user, test) + 1);
    }

    @HandleBeforeSave
    public void handleTestAttemptPreSave(TestAttempt attempt) {
        User user = CurrentUser.get();

        // The incoming entity is already merged with the request body: compare with the stored row
        TestAttemptRepository.AttemptSnapshot stored = testAttemptRepository.findSnapshotById(attempt.getId())
                .orElseThrow(() -> new AccessDeniedException("Attempt not found"));

        // Only the user taking the test can touch the attempt (administrators included)
        if (!user.getUsername().equals(stored.getOwnerUsername())) {
            throw new AccessDeniedException("Only the user taking the test can modify the attempt");
        }
        // Submitted and abandoned attempts are final
        if (stored.getStatus() != TestAttempt.Status.IN_PROGRESS) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A finished attempt cannot be modified");
        }
        // Identity of the attempt cannot be rewritten
        if (!stored.getOwnerUsername().equals(CurrentUser.usernameOf(attempt.getUser()))) {
            throw new AccessDeniedException("The user of an attempt cannot be changed");
        }
        if (attempt.getTest() == null || !stored.getTestId().equals(attempt.getTest().getId())) {
            throw new AccessDeniedException("The test of an attempt cannot be changed");
        }
        if (attempt.getAttemptType() != stored.getAttemptType()) {
            throw new AccessDeniedException("The type of an attempt cannot be changed");
        }

        // Allowed transitions from IN_PROGRESS: stay, SUBMITTED, ABANDONED
        if (attempt.getStatus() == TestAttempt.Status.SUBMITTED) {
            testAttemptService.submit(attempt); // grading, counters, countsForLeaderboard, submittedAt
        }
    }

    // A SUBMITTED attempt can no longer be modified, so reaching this point with SUBMITTED
    // means the attempt has just been submitted.
    @HandleAfterSave
    public void handleTestAttemptPostSave(TestAttempt attempt) {
        if (attempt.getStatus() != TestAttempt.Status.SUBMITTED) {
            return;
        }
        if (Boolean.TRUE.equals(attempt.getCountsForLeaderboard())) {
            leaderboardService.recalculateAll();
        }
        badgeService.evaluate(attempt.getUser());
    }

    private void checkAvailable(Test test, User user) {
        boolean isAuthor = user.getUsername().equals(CurrentUser.usernameOf(test.getAuthor()));
        boolean available = test.getStatus() == Test.Status.PUBLISHED
                && (Boolean.TRUE.equals(test.getIsPublic()) || isAuthor);
        if (!available) {
            throw new AccessDeniedException("This test is not available");
        }
    }
}
