package cat.udl.eps.softarch.demo.repository;

import cat.udl.eps.softarch.demo.domain.RateLimit;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

// Not exported: rate limit counters are internal and never reachable through the API.
@RepositoryRestResource(exported = false)
public interface RateLimitRepository extends CrudRepository<RateLimit, Long> {

    Optional<RateLimit> findByRateKey(String rateKey);
}
