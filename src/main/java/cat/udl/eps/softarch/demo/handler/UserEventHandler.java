package cat.udl.eps.softarch.demo.handler;

import cat.udl.eps.softarch.demo.domain.User;
import cat.udl.eps.softarch.demo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.HandleAfterDelete;
import org.springframework.data.rest.core.annotation.HandleAfterLinkSave;
import org.springframework.data.rest.core.annotation.HandleAfterSave;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeDelete;
import org.springframework.data.rest.core.annotation.HandleBeforeLinkSave;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Component
@RepositoryEventHandler
public class UserEventHandler {

    final Logger logger = LoggerFactory.getLogger(User.class);

    final UserRepository userRepository;
    final org.springframework.jdbc.core.JdbcTemplate jdbc;

    public UserEventHandler(UserRepository userRepository, org.springframework.jdbc.core.JdbcTemplate jdbc) {
        this.userRepository = userRepository;
        this.jdbc = jdbc;
    }

    @HandleBeforeCreate
    public void handleUserPreCreate(User user) {
        logger.info("Before creating: {}", user.toString());
    }

    @HandleBeforeSave
    public void handleUserPreSave(User user) {
        logger.info("Before updating user {}", user.getId());
        checkSelfOrAdmin(user, "Users can only modify their own profile");
        String storedPassword = jdbc.queryForObject(
                "SELECT password FROM demo_user WHERE id = ?", String.class, user.getId());
        if (!java.util.Objects.equals(storedPassword, user.getPassword()) || user.isPasswordReset()) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST, "Use password changes to update credentials");
        }
    }

    private void checkSelfOrAdmin(User target, String message) {
        User current = CurrentUser.get();
        if (!CurrentUser.isAdmin(current) && !current.getId().equals(target.getId())) {
            throw new AccessDeniedException(message);
        }
    }

    @HandleBeforeDelete
    public void handleUserPreDelete(User user) {
        logger.info("Before deleting: {}", user.toString());
        checkSelfOrAdmin(user, "Users can only delete their own account");
    }

    @HandleBeforeLinkSave
    public void handleUserPreLinkSave(User user, Object o) {
        logger.info("Before linking: {} to {}", user.toString(), o.toString());
    }

    @HandleAfterCreate
    public void handleUserPostCreate(User user) {
        logger.info("After creating: {}", user.toString());
        user.encodePassword();
        userRepository.save(user);
    }

    @HandleAfterSave
    public void handleUserPostSave(User user) {
        logger.info("After updating user {}", user.getId());
        if (user.isPasswordReset()) {
            user.encodePassword();
        }
        userRepository.save(user);
    }

    @HandleAfterDelete
    public void handleUserPostDelete(User user) {
        logger.info("After deleting: {}", user.toString());
    }

    @HandleAfterLinkSave
    public void handleUserPostLinkSave(User user, Object o) {
        logger.info("After linking: {} to {}", user.toString(), o.toString());
    }
}
