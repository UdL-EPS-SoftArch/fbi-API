package cat.udl.eps.softarch.demo.repository;

import cat.udl.eps.softarch.demo.domain.LoginAttempt;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

// Internal login throttling state (US-AUT-02): never exposed through the API
@RepositoryRestResource(exported = false)
public interface LoginAttemptRepository extends JpaRepository<LoginAttempt, String> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM LoginAttempt a WHERE a.accountHash = :accountHash")
    Optional<LoginAttempt> lockByAccountHash(@Param("accountHash") String accountHash);
}
