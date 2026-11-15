package cat.udl.eps.softarch.demo.handler;

import cat.udl.eps.softarch.demo.domain.User;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Helpers shared by the event handlers. Avoids a ClassCastException when the principal is
 * the String "anonymousUser" and avoids NullPointerExceptions on optional relations.
 */
final class CurrentUser {

    private CurrentUser() {
    }

    static User get() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof User user)) {
            throw new AccessDeniedException("Authentication required");
        }
        return user;
    }

    static boolean isAdmin(User user) {
        return user.getRole() == User.Role.ADMIN;
    }

    static String usernameOf(User user) {
        return user == null ? null : user.getUsername();
    }
}
