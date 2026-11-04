package cat.udl.eps.softarch.demo.repository;

import cat.udl.eps.softarch.demo.domain.Question;
import cat.udl.eps.softarch.demo.domain.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource
public interface QuestionRepository extends CrudRepository<Question, Long>, PagingAndSortingRepository<Question, Long> {

    // Questions are visible to the test author, admins, and when the test is published and public
    String VISIBILITY_CLAUSE =
        "q.test.author.username = ?#{authentication.name} " +
        "OR (q.test.status = 'PUBLISHED' AND q.test.isPublic = true) " +
        "OR ?#{hasRole('ADMIN')} = true";

    @Override
    @Query("SELECT q FROM Question q WHERE " + VISIBILITY_CLAUSE)
    Page<Question> findAll(Pageable pageable);

    @Override
    @Query("SELECT q FROM Question q WHERE q.id = :id AND (" + VISIBILITY_CLAUSE + ")")
    Optional<Question> findById(@Param("id") Long id);

    @Query("SELECT q FROM Question q WHERE q.test = :test AND (" + VISIBILITY_CLAUSE + ") ORDER BY q.orderIndex")
    Page<Question> findByTest(@Param("test") Test test, Pageable pageable);
}
