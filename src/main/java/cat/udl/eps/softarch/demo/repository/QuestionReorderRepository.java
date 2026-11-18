package cat.udl.eps.softarch.demo.repository;

import cat.udl.eps.softarch.demo.domain.QuestionReorder;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import java.util.Optional;

@RepositoryRestResource(path = "question-reorders")
public interface QuestionReorderRepository extends CrudRepository<QuestionReorder, Long> {
    @Override @RestResource(exported = false) Optional<QuestionReorder> findById(Long id);
    @Override @RestResource(exported = false) Iterable<QuestionReorder> findAll();
    @Override @RestResource(exported = false) void deleteById(Long id);
    @Override @RestResource(exported = false) void delete(QuestionReorder entity);
}
