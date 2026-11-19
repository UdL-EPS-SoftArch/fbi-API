package cat.udl.eps.softarch.demo.repository;

import cat.udl.eps.softarch.demo.domain.TestPreview;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import java.util.Optional;

@RepositoryRestResource(path = "test-previews")
public interface TestPreviewRepository extends TestPreviewOperations, CrudRepository<TestPreview, Long> {
    @Override @RestResource(exported = false) Optional<TestPreview> findById(Long id);
    @Override @RestResource(exported = false) Iterable<TestPreview> findAll();
    @Override @RestResource(exported = false) void deleteById(Long id);
    @Override @RestResource(exported = false) void delete(TestPreview preview);
}
