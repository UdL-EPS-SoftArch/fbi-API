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
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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

    /** Values as stored in the database (never the entity merged with the request body). */
    interface AnswerSnapshot {
        Long getAttemptId();

        Long getQuestionId();

        String getOwnerUsername();

        TestAttempt.Status getAttemptStatus();
    }

    @RestResource(exported = false)
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    @Query("SELECT att.id AS attemptId, q.id AS questionId, u.username AS ownerUsername, att.status AS attemptStatus " +
           "FROM AnswerAttempt a JOIN a.attempt att LEFT JOIN att.user u JOIN a.question q WHERE a.id = :id")
    Optional<AnswerSnapshot> findSnapshotById(@Param("id") Long id);

    /** All the answers of an attempt, without ownership filters (server-side grading). */
    @RestResource(exported = false)
    @Query("SELECT a FROM AnswerAttempt a JOIN FETCH a.question WHERE a.attempt.id = :attemptId")
    List<AnswerAttempt> listByAttemptId(@Param("attemptId") Long attemptId);
}
