package cat.udl.eps.softarch.demo.handler;

import cat.udl.eps.softarch.demo.domain.User;
import cat.udl.eps.softarch.demo.repository.UserRepository;
import cat.udl.eps.softarch.demo.service.EmailVerificationService;
import cat.udl.eps.softarch.demo.service.ReturnAddressService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.HandleAfterDelete;
import org.springframework.data.rest.core.annotation.HandleAfterLinkSave;
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

@Component
@RepositoryEventHandler
public class UserEventHandler {

    final Logger logger = LoggerFactory.getLogger(User.class);

    final UserRepository userRepository;
    final org.springframework.jdbc.core.JdbcTemplate jdbc;
    final EmailVerificationService emailVerificationService;

    public UserEventHandler(UserRepository userRepository, org.springframework.jdbc.core.JdbcTemplate jdbc,
                            EmailVerificationService emailVerificationService) {
        this.userRepository = userRepository;
        this.jdbc = jdbc;
        this.emailVerificationService = emailVerificationService;
    }

    @HandleBeforeCreate
    public void handleUserPreCreate(User user) {
        logger.info("Before creating: {}", user.toString());
        if (!Boolean.TRUE.equals(user.getAcceptTerms())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The Terms of Use and Privacy Policy must be accepted");
        }
        // Clear 409s instead of raw database constraint violations
        if (user.getUsername() != null && userRepository.existsByUsername(user.getUsername())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "This public name is already taken, please choose another one");
        }
        if (user.getEmail() != null && userRepository.existsByEmail(user.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "This email cannot be used, try logging in or recovering your password");
        }
        // Registration always creates students; the role cannot be chosen by the client
        user.setRole(User.Role.STUDENT);
        // New accounts cannot log in until they open the link sent by email (US-AUT-07)
        user.setEmailVerified(false);
        // Only internal addresses are kept, so the first login cannot send anyone outside (US-AUT-05)
        if (!ReturnAddressService.isInternal(user.getReturnTo())) {
            user.setReturnTo(null);
        }
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
    @HandleBeforeLinkDelete
    public void handleUserPreLinkChange(User user, Object o) {
        logger.info("Rejected link change on user {}", user.getId());
        CurrentUser.denyRelationChange();
    }

    @HandleAfterCreate
    public void handleUserPostCreate(User user) {
        logger.info("After creating: {}", user.toString());
        user.encodePassword();
        userRepository.save(user);
        emailVerificationService.sendVerification(user);
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
