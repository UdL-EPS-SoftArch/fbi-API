package cat.udl.eps.softarch.demo.repository;

import cat.udl.eps.softarch.demo.domain.EmailVerificationToken;
import cat.udl.eps.softarch.demo.domain.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

// Verification links (US-AUT-07): never exposed through the API
@RepositoryRestResource(exported = false)
public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM EmailVerificationToken t JOIN FETCH t.user WHERE t.tokenHash = :tokenHash")
    Optional<EmailVerificationToken> findForUpdate(@Param("tokenHash") String tokenHash);

    // Only the latest link of a user works: the previous ones are deleted
    @Modifying
    @Query("DELETE FROM EmailVerificationToken t WHERE t.user = :user")
    void deleteByUser(@Param("user") User user);
}
