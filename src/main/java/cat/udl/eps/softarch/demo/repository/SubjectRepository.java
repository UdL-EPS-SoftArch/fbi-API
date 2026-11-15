package cat.udl.eps.softarch.demo.repository;

import cat.udl.eps.softarch.demo.domain.Subject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

// Subjects are public: anyone can read them, only admins modify them (see SubjectEventHandler).
// Archived subjects (active = false) are hidden from browsing and search, except for administrators.
@RepositoryRestResource
public interface SubjectRepository extends CrudRepository<Subject, Long>, PagingAndSortingRepository<Subject, Long> {

    String VISIBILITY_CLAUSE = "s.active = true OR ?#{hasRole('ADMIN')} = true";

    @Override
    @Query("SELECT s FROM Subject s WHERE " + VISIBILITY_CLAUSE)
    Page<Subject> findAll(Pageable pageable);

    // findById and findByName are not filtered: tests and leaderboards of an archived subject keep resolving it
    List<Subject> findByName(@Param("name") String name);

    @Query("SELECT s FROM Subject s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :text, '%')) AND (" + VISIBILITY_CLAUSE + ")")
    List<Subject> findByNameContainingIgnoreCase(@Param("text") String text);

    boolean existsByNameIgnoreCase(@Param("name") String name);

    // Internal use only (SubjectEventHandler): is the name used by any other subject?
    @RestResource(exported = false)
    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
}
