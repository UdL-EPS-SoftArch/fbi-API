package cat.udl.eps.softarch.demo.repository;

import cat.udl.eps.softarch.demo.domain.Subject;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

// Subjects are public: anyone can read them, only admins modify them (see SubjectEventHandler)
@RepositoryRestResource
public interface SubjectRepository extends CrudRepository<Subject, Long>, PagingAndSortingRepository<Subject, Long> {

    List<Subject> findByName(@Param("name") String name);

    List<Subject> findByNameContainingIgnoreCase(@Param("text") String text);
}
