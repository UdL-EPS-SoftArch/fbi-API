package cat.udl.eps.softarch.demo.repository;

import cat.udl.eps.softarch.demo.domain.AnswerAttempt;
import cat.udl.eps.softarch.demo.domain.TestAttempt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource
public interface AnswerAttemptRepository extends CrudRepository<AnswerAttempt, Long>, PagingAndSortingRepository<AnswerAttempt, Long> {

    // The user of an answer is the user of its test attempt
    String OWNERSHIP_CLAUSE =
        "a.attempt.user.username = ?#{authentication.name} " +
        "OR ?#{hasRole('ADMIN')} = true";

    @Override
    @Query("SELECT a FROM AnswerAttempt a WHERE " + OWNERSHIP_CLAUSE)
    Page<AnswerAttempt> findAll(Pageable pageable);

    @Override
    @Query("SELECT a FROM AnswerAttempt a WHERE a.id = :id AND (" + OWNERSHIP_CLAUSE + ")")
    Optional<AnswerAttempt> findById(@Param("id") Long id);

    @Query("SELECT a FROM AnswerAttempt a WHERE a.attempt = :attempt AND (" + OWNERSHIP_CLAUSE + ")")
    Page<AnswerAttempt> findByAttempt(@Param("attempt") TestAttempt attempt, Pageable pageable);
}
