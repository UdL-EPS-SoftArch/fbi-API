package cat.udl.eps.softarch.demo.repository;

import cat.udl.eps.softarch.demo.domain.Subject;
import cat.udl.eps.softarch.demo.domain.Test;
import cat.udl.eps.softarch.demo.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;
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

    @Query("SELECT t FROM Test t WHERE t.subject = :subject AND t.status = 'PUBLISHED' AND t.isPublic = true")
    Page<Test> findPublicPublishedBySubject(@Param("subject") Subject subject, Pageable pageable);

    @Query("SELECT t FROM Test t LEFT JOIN t.author author WHERE LOWER(t.title) LIKE LOWER(CONCAT('%', :text, '%')) AND (" + VISIBILITY_CLAUSE + ")")
    Page<Test> findByTitleContaining(@Param("text") String text, Pageable pageable);

    /*
     * Catalog search (US-COM-04): published public tests only. Every filter is optional and they
     * are combined with AND; a test matches the tag filter if it has ANY of the selected tags.
     * The keyword is compared without case or accents (see SearchText).
     */
    String CATALOG_FILTERS =
        "t.status = 'PUBLISHED' AND t.isPublic = true " +
        "AND t.searchTitle LIKE CONCAT('%', :#{T(cat.udl.eps.softarch.demo.domain.SearchText).normalize(#text)}, '%') " +
        "AND (:subject IS NULL OR t.subject = :subject) " +
        "AND (:type IS NULL OR t.testType = :type) " +
        "AND (:difficulty IS NULL OR t.difficulty = :difficulty) " +
        "AND (:#{T(cat.udl.eps.softarch.demo.domain.SearchText).hasTags(#tags)} = false " +
        "     OR EXISTS (SELECT 1 FROM Test tagged JOIN tagged.tags tag WHERE tagged = t " +
        "                AND tag IN :#{T(cat.udl.eps.softarch.demo.domain.SearchText).tagFilter(#tags)}))";

    /** Sorted with the usual "sort" parameter, e.g. sort=publishedAt,desc or sort=title. */
    @RestResource(path = "catalog", rel = "catalog")
    @Query(value = "SELECT t FROM Test t WHERE " + CATALOG_FILTERS,
           countQuery = "SELECT COUNT(t) FROM Test t WHERE " + CATALOG_FILTERS)
    Page<Test> searchCatalog(@Param("text") String text, @Param("subject") Subject subject,
                             @Param("type") Test.TestType type, @Param("difficulty") Test.Difficulty difficulty,
                             @Param("tags") List<String> tags, Pageable pageable);

    /** Most popular first: number of completed (submitted) regular attempts, descending. */
    @RestResource(path = "catalogByPopularity", rel = "catalogByPopularity")
    @Query(value = "SELECT t FROM Test t LEFT JOIN TestAttempt a " +
                   "ON a.test = t AND a.status = 'SUBMITTED' AND a.attemptType = 'REGULAR' " +
                   "WHERE " + CATALOG_FILTERS + " GROUP BY t ORDER BY COUNT(a) DESC, t.id ASC",
           countQuery = "SELECT COUNT(t) FROM Test t WHERE " + CATALOG_FILTERS)
    Page<Test> searchCatalogByPopularity(@Param("text") String text, @Param("subject") Subject subject,
                                         @Param("type") Test.TestType type,
                                         @Param("difficulty") Test.Difficulty difficulty,
                                         @Param("tags") List<String> tags, Pageable pageable);

    // Tests saved before the search title existed (backfilled at startup); internal use only
    @RestResource(exported = false)
    List<Test> findBySearchTitleIsNull();

    @RestResource(exported = false)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM Test t WHERE t.id = :id")
    Optional<Test> lockForContentChange(@Param("id") Long id);

    /** Derived value: only written by the server when the questions of the test change. */
    @RestResource(exported = false)
    @Modifying
    @Transactional
    @Query("UPDATE Test t SET t.maxScore = :maxScore WHERE t.id = :id")
    void updateMaxScore(@Param("id") Long id, @Param("maxScore") Integer maxScore);

    @RestResource(path = "featured", rel = "featured")
    @Query("SELECT t FROM TestAttempt a JOIN a.test t " +
           "WHERE t.status = 'PUBLISHED' AND t.isPublic = true " +
           "AND a.status = 'SUBMITTED' AND a.attemptType = 'REGULAR' " +
           "AND a.submittedAt >= ?#{T(java.time.Instant).now().minus(30, T(java.time.temporal.ChronoUnit).DAYS)} " +
           "AND a.submittedAt <= ?#{T(java.time.Instant).now()} " +
           "GROUP BY t ORDER BY COUNT(a) DESC, t.id ASC")
    List<Test> findFeatured();

    // Not filtered by visibility (counts drafts and private tests too): internal use only (SubjectEventHandler)
    @RestResource(exported = false)
    boolean existsBySubject(Subject subject);

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
