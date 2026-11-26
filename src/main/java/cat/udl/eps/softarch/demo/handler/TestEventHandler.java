package cat.udl.eps.softarch.demo.handler;

import java.time.Instant;

import org.springframework.data.rest.core.annotation.HandleAfterSave;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeDelete;
import org.springframework.data.rest.core.annotation.HandleBeforeLinkDelete;
import org.springframework.data.rest.core.annotation.HandleBeforeLinkSave;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import cat.udl.eps.softarch.demo.domain.Test;
import cat.udl.eps.softarch.demo.domain.User;
import cat.udl.eps.softarch.demo.repository.TestRepository;
import cat.udl.eps.softarch.demo.service.LeaderboardService;
import cat.udl.eps.softarch.demo.service.TestContentService;

@Component
@RepositoryEventHandler
public class TestEventHandler {
    final TestRepository testRepository;
    final LeaderboardService leaderboardService;
    final TestContentService testContentService;

    public TestEventHandler(TestRepository testRepository, LeaderboardService leaderboardService,
                            TestContentService testContentService) {
        this.testRepository = testRepository;
        this.leaderboardService = leaderboardService;
        this.testContentService = testContentService;
    }

    @HandleBeforeCreate
    public void handleTestPreCreate(Test test) {
        User user = CurrentUser.get();
        test.setAuthor(user);
        checkOfficialByAdmin(test, user);
        // An archived subject keeps its tests, but no new ones can be added to it
        if (test.getSubject() != null && !test.getSubject().isActive()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Tests cannot be created in an archived subject");
        }
        // A new test has no questions yet, so it can only be published once they are added
        if (test.getStatus() == Test.Status.PUBLISHED) {
            testContentService.checkPublishable(test);
        }
        setPublishedAt(test);
    }

    @HandleBeforeSave
    public void handleTestPreSave(Test test) {
        User user = CurrentUser.get();
        boolean admin = CurrentUser.isAdmin(user);

        // The incoming entity is already merged with the request body: the ownership and the
        // lifecycle rules are checked against what is stored in the database.
        TestRepository.TestSnapshot stored = testRepository.findSnapshotById(test.getId())
                .orElseThrow(() -> new AccessDeniedException("Test not found"));

        if (!admin) {
            if (!user.getUsername().equals(stored.getAuthorUsername())) {
                throw new AccessDeniedException("Only the author can modify the test");
            }
            if (stored.getTestType() == Test.TestType.OFFICIAL) {
                throw new AccessDeniedException("Only administrators can manage official tests");
            }
            if (!user.getUsername().equals(CurrentUser.usernameOf(test.getAuthor()))) {
                throw new AccessDeniedException("The author of a test cannot be changed");
            }
            // Withdrawn by moderation: only an administrator can bring it back
            if (stored.getStatus() == Test.Status.WITHDRAWN && test.getStatus() != Test.Status.WITHDRAWN) {
                throw new AccessDeniedException("Only administrators can restore a withdrawn test");
            }
        }
        checkOfficialByAdmin(test, user);
        // Validated when the test becomes published (first publication or republication), with the
        // whole checklist; an already published test only has to keep its visibility rule
        if (test.getStatus() == Test.Status.PUBLISHED && stored.getStatus() != Test.Status.PUBLISHED) {
            testContentService.checkPublishable(test);
        } else {
            testContentService.checkVisibility(test);
        }
        setPublishedAt(test);
    }

    @HandleBeforeLinkSave
    @HandleBeforeLinkDelete
    public void handleTestPreLinkChange(Test test, Object linked) {
        CurrentUser.denyRelationChange();
    }

    @HandleBeforeDelete
    public void handleTestPreDelete(Test test) {
        User user = CurrentUser.get();
        if (!CurrentUser.isAdmin(user) && !user.getUsername().equals(CurrentUser.usernameOf(test.getAuthor()))) {
            throw new AccessDeniedException("Only the author can modify the test");
        }
    }

    // Publishing, withdrawing or republishing an official test changes who is eligible for the rankings
    @HandleAfterSave
    public void handleTestPostSave(Test test) {
        if (test.getTestType() == Test.TestType.OFFICIAL) {
            leaderboardService.recalculateAll();
        }
    }

    private void checkOfficialByAdmin(Test test, User user) {
        if (test.getTestType() == Test.TestType.OFFICIAL && !CurrentUser.isAdmin(user)) {
            throw new AccessDeniedException("Only administrators can manage official tests");
        }
    }

    private void setPublishedAt(Test test) {
        if (test.getStatus() == Test.Status.PUBLISHED && test.getPublishedAt() == null) {
            test.setPublishedAt(Instant.now());
        }
    }
}
