package cat.udl.eps.softarch.demo.config;

import cat.udl.eps.softarch.demo.domain.User;
import cat.udl.eps.softarch.demo.service.SessionService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Authenticates requests that carry "Authorization: Bearer <session token>" (US-AUT-06).
 * Requests with HTTP Basic credentials or without credentials are left untouched. Not a
 * bean, so that it only runs inside the Spring Security filter chain.
 */
public class SessionTokenAuthenticationFilter extends OncePerRequestFilter {
    static final String BEARER_PREFIX = "Bearer ";

    private final SessionService sessionService;
    private final AuthenticationEntryPoint entryPoint;

    public SessionTokenAuthenticationFilter(SessionService sessionService, AuthenticationEntryPoint entryPoint) {
        this.sessionService = sessionService;
        this.entryPoint = entryPoint;
    }

    static String bearerToken(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.regionMatches(true, 0, BEARER_PREFIX, 0, BEARER_PREFIX.length())) {
            return null;
        }
        String token = header.substring(BEARER_PREFIX.length()).trim();
        return token.isEmpty() ? null : token;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String token = bearerToken(request);
        if (token == null) {
            chain.doFilter(request, response);
            return;
        }
        try {
            User user = sessionService.authenticate(token);
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(
                    UsernamePasswordAuthenticationToken.authenticated(user, null, user.getAuthorities()));
            SecurityContextHolder.setContext(context);
        } catch (AuthenticationException exception) {
            SecurityContextHolder.clearContext();
            entryPoint.commence(request, response, exception);
            return;
        }
        chain.doFilter(request, response);
    }
}
