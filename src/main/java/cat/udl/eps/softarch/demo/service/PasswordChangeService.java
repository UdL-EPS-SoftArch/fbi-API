package cat.udl.eps.softarch.demo.service;

import cat.udl.eps.softarch.demo.domain.User;
import cat.udl.eps.softarch.demo.repository.UserRepository;
import java.nio.charset.StandardCharsets;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PasswordChangeService {
    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final ApplicationEventPublisher events;

    public PasswordChangeService(UserRepository users, PasswordEncoder encoder, ApplicationEventPublisher events) {
        this.users = users;
        this.encoder = encoder;
        this.events = events;
    }

    @Transactional
    public void change(String current, String next) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = users.findByUsernameForUpdate(username).orElseThrow();
        if (current == null || current.isBlank() || !encoder.matches(current, user.getPassword())
                || next == null || next.isBlank() || next.length() < 8 || next.length() > 256
                || next.getBytes(StandardCharsets.UTF_8).length > 72
                || encoder.matches(next, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid password");
        }
        user.setPassword(encoder.encode(next));
        users.save(user);
        events.publishEvent(new PasswordChangedMail(user.getEmail()));
    }
}
