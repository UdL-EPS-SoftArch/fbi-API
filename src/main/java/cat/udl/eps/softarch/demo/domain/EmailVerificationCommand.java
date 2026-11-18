package cat.udl.eps.softarch.demo.domain;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

/** Public commands of the email verification (US-AUT-07), exposed like the password reset ones. */
@MappedSuperclass
public abstract class EmailVerificationCommand extends UriEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Override
    public Long getId() {
        return id;
    }
}
