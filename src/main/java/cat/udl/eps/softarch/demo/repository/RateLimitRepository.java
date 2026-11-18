package cat.udl.eps.softarch.demo.repository;

import cat.udl.eps.softarch.demo.domain.RateLimit;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

// Not exported: rate limit counters are internal and never reachable through the API.
@RepositoryRestResource(exported = false)
public interface RateLimitRepository extends CrudRepository<RateLimit, Long> {

    Optional<RateLimit> findByRateKey(String rateKey);

    // Locks the row until the transaction ends, so concurrent requests count one by one.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM RateLimit r WHERE r.rateKey = :rateKey")
    Optional<RateLimit> lockByRateKey(@Param("rateKey") String rateKey);
}
