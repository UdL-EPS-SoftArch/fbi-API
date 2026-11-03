package cat.udl.eps.softarch.demo.repository;

import cat.udl.eps.softarch.demo.domain.Subject;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(exported = false)
public interface SubjectRepository extends CrudRepository<Subject, Long> {
}
