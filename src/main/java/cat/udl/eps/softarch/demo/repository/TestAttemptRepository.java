package cat.udl.eps.softarch.demo.repository;

import cat.udl.eps.softarch.demo.domain.Test;
import cat.udl.eps.softarch.demo.domain.TestAttempt;
import cat.udl.eps.softarch.demo.domain.User;
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
public interface TestAttemptRepository extends CrudRepository<TestAttempt, Long>, PagingAndSortingRepository<TestAttempt, Long> {

    String OWNERSHIP_CLAUSE =
        "a.user.username = ?#{authentication.name} " +
        "OR ?#{hasRole('ADMIN')} = true";

    @Override
    @Query("SELECT a FROM TestAttempt a WHERE " + OWNERSHIP_CLAUSE)
    Page<TestAttempt> findAll(Pageable pageable);

    @Override
    @Query("SELECT a FROM TestAttempt a WHERE a.id = :id AND (" + OWNERSHIP_CLAUSE + ")")
    Optional<TestAttempt> findById(@Param("id") Long id);

    @Query("SELECT a FROM TestAttempt a WHERE a.user = :user AND (" + OWNERSHIP_CLAUSE + ")")
    Page<TestAttempt> findByUser(@Param("user") User user, Pageable pageable);

    @Query("SELECT a FROM TestAttempt a WHERE a.test = :test AND (" + OWNERSHIP_CLAUSE + ")")
    Page<TestAttempt> findByTest(@Param("test") Test test, Pageable pageable);

    @RestResource(exported = false)
    long countByUserAndTest(User user, Test test);

    /** Values as stored in the database (never the entity merged with the request body). */
    interface AttemptSnapshot {
        String getOwnerUsername();

        TestAttempt.Status getStatus();

        TestAttempt.AttemptType getAttemptType();

        Long getTestId();
    }

    @RestResource(exported = false)
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    @Query("SELECT u.username AS ownerUsername, a.status AS status, a.attemptType AS attemptType, t.id AS testId " +
           "FROM TestAttempt a LEFT JOIN a.user u LEFT JOIN a.test t WHERE a.id = :id")
    Optional<AttemptSnapshot> findSnapshotById(@Param("id") Long id);

    /** Submitted attempts that count for the rankings, for tests that are currently eligible (2.4.2). */
    @RestResource(exported = false)
    @Query("SELECT a FROM TestAttempt a JOIN FETCH a.user u JOIN FETCH a.test t JOIN FETCH t.subject s " +
           "WHERE a.countsForLeaderboard = true AND a.status = 'SUBMITTED' AND a.attemptType = 'REGULAR' " +
           "AND t.testType = 'OFFICIAL' AND t.status = 'PUBLISHED' AND t.isPublic = true AND u.role = 'STUDENT' " +
           "ORDER BY u.id, t.id, a.submittedAt, a.id")
    List<TestAttempt> listEligibleForRanking();

    /** Regular submitted attempts of a user on official tests (badge evaluation, 2.4.6). */
    @RestResource(exported = false)
    @Query("SELECT a FROM TestAttempt a JOIN FETCH a.test t " +
           "WHERE a.user.id = :userId AND a.status = 'SUBMITTED' AND a.attemptType = 'REGULAR' " +
           "AND t.testType = 'OFFICIAL'")
    List<TestAttempt> listSubmittedOfficialByUserId(@Param("userId") Long userId);
}
