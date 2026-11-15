package cat.udl.eps.softarch.demo.repository;

import cat.udl.eps.softarch.demo.domain.Subject;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

// Subjects are public: anyone can read them, only admins modify them (see SubjectEventHandler)
@RepositoryRestResource
public interface SubjectRepository extends CrudRepository<Subject, Long>, PagingAndSortingRepository<Subject, Long> {

    @Override
    @Query("select s from Subject s where s.archived = false or s.archived is null")
    Page<Subject> findAll(Pageable pageable);

    List<Subject> findByName(@Param("name") String name);

    List<Subject> findByNameContainingIgnoreCase(@Param("text") String text);

    boolean existsByNameIgnoreCase(@Param("name") String name);

    // Internal use only (SubjectEventHandler): is the name used by any other subject?
    @RestResource(exported = false)
    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
}
