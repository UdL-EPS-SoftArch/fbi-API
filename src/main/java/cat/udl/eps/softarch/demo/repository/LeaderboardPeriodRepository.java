package cat.udl.eps.softarch.demo.repository;

import cat.udl.eps.softarch.demo.domain.LeaderboardPeriod;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

@RepositoryRestResource
public interface LeaderboardPeriodRepository extends CrudRepository<LeaderboardPeriod, Long> {
    @RestResource(exported = false)
    Optional<LeaderboardPeriod> findByEndsAtIsNull();

    @Override
    @RestResource(exported = false)
    <S extends LeaderboardPeriod> S save(S period);

    @Override
    @RestResource(exported = false)
    void deleteById(Long id);

    @Override
    @RestResource(exported = false)
    void delete(LeaderboardPeriod period);
}
