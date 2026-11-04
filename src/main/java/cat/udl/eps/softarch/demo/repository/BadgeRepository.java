package cat.udl.eps.softarch.demo.repository;

import cat.udl.eps.softarch.demo.domain.Badge;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

// Badges are public: anyone can read them, only admins modify them (see BadgeEventHandler)
@RepositoryRestResource
public interface BadgeRepository extends CrudRepository<Badge, Long>, PagingAndSortingRepository<Badge, Long> {

    List<Badge> findByName(@Param("name") String name);

    List<Badge> findByIsActive(@Param("isActive") Boolean isActive);
}
