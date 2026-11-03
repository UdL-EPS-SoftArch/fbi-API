package cat.udl.eps.softarch.demo.repository;

import cat.udl.eps.softarch.demo.domain.PlatformTest;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

@RepositoryRestResource(path = "platformTests", collectionResourceRel = "platformTests")
public interface PlatformTestRepository extends CrudRepository<PlatformTest, Long> {
    @RestResource(path = "featured", rel = "featured")
    @Query("SELECT t FROM PlatformTest t WHERE t.featured = true AND t.published = true ORDER BY t.id ASC")
    List<PlatformTest> findFeatured();

    @Override
    @RestResource(exported = false)
    Iterable<PlatformTest> findAll();

    @Override
    @RestResource(exported = false)
    java.util.Optional<PlatformTest> findById(Long id);

    @Override
    @RestResource(exported = false)
    <S extends PlatformTest> S save(S entity);

    @Override
    @RestResource(exported = false)
    void deleteById(Long id);

    @Override
    @RestResource(exported = false)
    void delete(PlatformTest entity);
}
