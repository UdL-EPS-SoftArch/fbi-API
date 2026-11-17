package cat.udl.eps.softarch.demo.repository;

import cat.udl.eps.softarch.demo.domain.PasswordResetRateLimit;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import java.util.Optional;

@RepositoryRestResource(exported = false)
public interface PasswordResetRateLimitRepository extends JpaRepository<PasswordResetRateLimit, String> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from PasswordResetRateLimit r where r.emailHash = :emailHash")
    Optional<PasswordResetRateLimit> lockByEmailHash(@Param("emailHash") String emailHash);
}
