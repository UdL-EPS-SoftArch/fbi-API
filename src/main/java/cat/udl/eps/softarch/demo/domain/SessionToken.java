package cat.udl.eps.softarch.demo.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Duration;
import java.time.Instant;

/**
 * Server-side session created at login and deleted at logout (US-AUT-06). Only the SHA-256
 * hash of the token is stored, never the token itself.
 */
@Entity
@Table(name = "session_token")
public class SessionToken {
    public static final Duration LIFETIME = Duration.ofDays(7);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String tokenHash;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private User user;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant expiresAt;

    protected SessionToken() {
    }

    public SessionToken(String tokenHash, User user, Instant now) {
        this.tokenHash = tokenHash;
        this.user = user;
        this.createdAt = now;
        this.expiresAt = now.plus(LIFETIME);
    }

    public User getUser() {
        return user;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public boolean isValidAt(Instant now) {
        return now.isBefore(expiresAt);
    }
}
