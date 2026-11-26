package cat.udl.eps.softarch.demo.controller;

import cat.udl.eps.softarch.demo.domain.User;
import cat.udl.eps.softarch.demo.service.ReturnAddressService;
import cat.udl.eps.softarch.demo.service.SessionService;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

/**
 * Starts a server-side session (US-AUT-06): logging in with the email (or username) and
 * password returns a session token, which is revoked by POST /logout. The answer also says
 * where to send the user next (US-AUT-05), from the optional "returnTo" parameter.
 */
@BasePathAwareController
public class SessionController {
    private final SessionService sessionService;
    private final ReturnAddressService returnAddressService;

    public SessionController(SessionService sessionService, ReturnAddressService returnAddressService) {
        this.sessionService = sessionService;
        this.returnAddressService = returnAddressService;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record StartedSession(String token, Instant expiresAt, String redirectTo, String notice) {
    }

    @PostMapping("/sessions")
    public ResponseEntity<StartedSession> startSession(HttpServletRequest request,
                                                       @RequestParam(required = false) String returnTo) {
        // Only right after checking the password: a session token cannot extend itself
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorization == null || !authorization.regionMatches(true, 0, "Basic ", 0, 6)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Log in with your email and password to start a session");
        }
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        SessionService.IssuedSession session = sessionService.issue(user);
        ReturnAddressService.Destination destination = returnAddressService.afterLogin(user, returnTo);
        return ResponseEntity.status(HttpStatus.CREATED).body(new StartedSession(
                session.token(), session.expiresAt(), destination.redirectTo(), destination.notice()));
    }
}
