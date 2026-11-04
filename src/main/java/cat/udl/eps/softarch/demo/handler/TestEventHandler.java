package cat.udl.eps.softarch.demo.handler;

import cat.udl.eps.softarch.demo.domain.Test;
import cat.udl.eps.softarch.demo.domain.User;
import cat.udl.eps.softarch.demo.repository.TestRepository;
import org.springframework.data.rest.core.annotation.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RepositoryEventHandler
public class TestEventHandler {
    final TestRepository testRepository;

    public TestEventHandler(TestRepository testRepository) {
        this.testRepository = testRepository;
    }

    @HandleBeforeCreate
    public void handleTestPreCreate(Test test) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        test.setAuthor(user);
        checkOfficialByAdmin(test, user);
        setPublishedAt(test);
    }

    @HandleBeforeSave
    public void handleTestPreSave(Test test) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        checkAuthorOrAdmin(test, user);
        checkOfficialByAdmin(test, user);
        setPublishedAt(test);
    }

    @HandleBeforeDelete
    public void handleTestPreDelete(Test test) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        checkAuthorOrAdmin(test, user);
    }

    private void checkAuthorOrAdmin(Test test, User user) {
        if (user.getRole() != User.Role.ADMIN
                && !user.getUsername().equals(test.getAuthor().getUsername())) {
            throw new AccessDeniedException("Only the author can modify the test");
        }
    }

    private void checkOfficialByAdmin(Test test, User user) {
        if (test.getTestType() == Test.TestType.OFFICIAL && user.getRole() != User.Role.ADMIN) {
            throw new AccessDeniedException("Only administrators can manage official tests");
        }
    }

    private void setPublishedAt(Test test) {
        if (test.getStatus() == Test.Status.PUBLISHED && test.getPublishedAt() == null) {
            test.setPublishedAt(Instant.now());
        }
    }
}
