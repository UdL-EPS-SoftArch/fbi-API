package cat.udl.eps.softarch.demo.repository;

import cat.udl.eps.softarch.demo.domain.RateLimit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(exported = false)
public interface RateLimitRepository extends JpaRepository<RateLimit, String> {
}
