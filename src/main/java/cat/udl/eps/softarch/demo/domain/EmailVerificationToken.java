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
 * Link sent by email to verify an account (US-AUT-07). Valid for 24 hours; only the SHA-256
 * hash of the token is stored. A user only keeps the token of the latest link sent.
 */
@Entity
@Table(name = "email_verification_token")
public class EmailVerificationToken {
    public static final Duration LIFETIME = Duration.ofHours(24);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String tokenHash;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private User user;

    @Column(nullable = false)
    private Instant expiresAt;

    protected EmailVerificationToken() {
    }

    public EmailVerificationToken(String tokenHash, User user, Instant now) {
        this.tokenHash = tokenHash;
        this.user = user;
        this.expiresAt = now.plus(LIFETIME);
    }

    public User getUser() {
        return user;
    }

    public boolean isValidAt(Instant now) {
        return now.isBefore(expiresAt);
    }
}
