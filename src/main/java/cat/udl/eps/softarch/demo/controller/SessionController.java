package cat.udl.eps.softarch.demo.controller;

import cat.udl.eps.softarch.demo.domain.User;
import cat.udl.eps.softarch.demo.service.SessionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;

/**
 * Starts a server-side session (US-AUT-06): logging in with the email (or username) and
 * password returns a session token, which is revoked by POST /logout.
 */
@BasePathAwareController
public class SessionController {
    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @PostMapping("/sessions")
    public ResponseEntity<SessionService.IssuedSession> startSession(HttpServletRequest request) {
        // Only right after checking the password: a session token cannot extend itself
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorization == null || !authorization.regionMatches(true, 0, "Basic ", 0, 6)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Log in with your email and password to start a session");
        }
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.status(HttpStatus.CREATED).body(sessionService.issue(user));
    }
}
