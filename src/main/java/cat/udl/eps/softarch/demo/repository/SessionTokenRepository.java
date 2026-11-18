package cat.udl.eps.softarch.demo.repository;

import cat.udl.eps.softarch.demo.domain.SessionToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

// Server-side sessions (US-AUT-06): never exposed through the API
@RepositoryRestResource(exported = false)
public interface SessionTokenRepository extends JpaRepository<SessionToken, Long> {

    // The user is fetched with the token: it becomes the principal outside any transaction
    @Query("SELECT s FROM SessionToken s JOIN FETCH s.user WHERE s.tokenHash = :tokenHash")
    Optional<SessionToken> findByTokenHash(@Param("tokenHash") String tokenHash);

    @Modifying
    @Query("DELETE FROM SessionToken s WHERE s.tokenHash = :tokenHash")
    int deleteByTokenHash(@Param("tokenHash") String tokenHash);
}
