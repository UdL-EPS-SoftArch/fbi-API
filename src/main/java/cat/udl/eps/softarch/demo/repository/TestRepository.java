package cat.udl.eps.softarch.demo.repository;

import cat.udl.eps.softarch.demo.domain.Subject;
import cat.udl.eps.softarch.demo.domain.Test;
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
public interface TestRepository extends CrudRepository<Test, Long>, PagingAndSortingRepository<Test, Long> {

    String VISIBILITY_CLAUSE =
        "author.username = ?#{authentication.name} " +
        "OR (t.status = 'PUBLISHED' AND t.isPublic = true) " +
        "OR ?#{hasRole('ADMIN')} = true";

    @Override
    @Query("SELECT t FROM Test t LEFT JOIN t.author author WHERE " + VISIBILITY_CLAUSE)
    Page<Test> findAll(Pageable pageable);

    @Override
    @Query("SELECT t FROM Test t LEFT JOIN t.author author WHERE t.id = :id AND (" + VISIBILITY_CLAUSE + ")")
    Optional<Test> findById(@Param("id") Long id);

    @Query("SELECT t FROM Test t LEFT JOIN t.author author WHERE t.author = :user AND (" + VISIBILITY_CLAUSE + ")")
    Page<Test> findByAuthor(@Param("user") User author, Pageable pageable);

    @Query("SELECT t FROM Test t LEFT JOIN t.author author WHERE t.subject = :subject AND (" + VISIBILITY_CLAUSE + ")")
    Page<Test> findBySubject(@Param("subject") Subject subject, Pageable pageable);

    @Query("SELECT t FROM Test t LEFT JOIN t.author author WHERE LOWER(t.title) LIKE LOWER(CONCAT('%', :text, '%')) AND (" + VISIBILITY_CLAUSE + ")")
    Page<Test> findByTitleContaining(@Param("text") String text, Pageable pageable);

    @RestResource(path = "featured", rel = "featured")
    @Query("SELECT t FROM TestAttempt a JOIN a.test t " +
           "WHERE t.status = 'PUBLISHED' AND t.isPublic = true " +
           "AND a.status = 'SUBMITTED' AND a.attemptType = 'REGULAR' " +
           "AND a.submittedAt >= ?#{T(java.time.Instant).now().minus(30, T(java.time.temporal.ChronoUnit).DAYS)} " +
           "AND a.submittedAt <= ?#{T(java.time.Instant).now()} " +
           "GROUP BY t ORDER BY COUNT(a) DESC, t.id ASC")
    List<Test> findFeatured();

    // Not filtered by visibility: internal use only (the public search is findByTitleContaining)
    @RestResource(exported = false)
    List<Test> findByTitle(String title);

    /** Values as stored in the database (never the entity merged with the request body). */
    interface TestSnapshot {
        String getAuthorUsername();

        Test.Status getStatus();

        Test.TestType getTestType();
    }

    // REQUIRES_NEW: runs in its own session, so it always sees the committed row and never the
    // entity that Spring Data REST has already merged with the JSON of the current request.
    @RestResource(exported = false)
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    @Query("SELECT u.username AS authorUsername, t.status AS status, t.testType AS testType " +
           "FROM Test t LEFT JOIN t.author u WHERE t.id = :id")
    Optional<TestSnapshot> findSnapshotById(@Param("id") Long id);
}
