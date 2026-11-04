package cat.udl.eps.softarch.demo.handler;

import cat.udl.eps.softarch.demo.domain.TestAttempt;
import cat.udl.eps.softarch.demo.domain.User;
import cat.udl.eps.softarch.demo.repository.TestAttemptRepository;
import org.springframework.data.rest.core.annotation.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RepositoryEventHandler
public class TestAttemptEventHandler {
    final TestAttemptRepository testAttemptRepository;

    public TestAttemptEventHandler(TestAttemptRepository testAttemptRepository) {
        this.testAttemptRepository = testAttemptRepository;
    }

    @HandleBeforeCreate
    public void handleTestAttemptPreCreate(TestAttempt attempt) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        attempt.setUser(user);
        attempt.setStatus(TestAttempt.Status.IN_PROGRESS);
        attempt.setStartedAt(Instant.now());
        if (user.getRole() == User.Role.ADMIN) {
            attempt.setAttemptType(TestAttempt.AttemptType.VALIDATION);
        }
        if (attempt.getTest() != null) {
            attempt.setAttemptNumber((int) testAttemptRepository.countByUserAndTest(user, attempt.getTest()) + 1);
        }
    }

    @HandleBeforeSave
    public void handleTestAttemptPreSave(TestAttempt attempt) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user.getRole() != User.Role.ADMIN
                && !user.getUsername().equals(attempt.getUser().getUsername())) {
            throw new AccessDeniedException("Only the user taking the test can modify the attempt");
        }
        if (attempt.getStatus() == TestAttempt.Status.SUBMITTED && attempt.getSubmittedAt() == null) {
            attempt.setSubmittedAt(Instant.now());
        }
    }
}
