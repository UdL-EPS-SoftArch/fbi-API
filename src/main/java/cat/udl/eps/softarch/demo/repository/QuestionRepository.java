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
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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

    /** Values as stored in the database (never the entity merged with the request body). */
    interface QuestionSnapshot {
        Long getTestId();

        String getTestAuthorUsername();
    }

    @RestResource(exported = false)
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    @Query("SELECT t.id AS testId, au.username AS testAuthorUsername " +
           "FROM Question q JOIN q.test t LEFT JOIN t.author au WHERE q.id = :id")
    Optional<QuestionSnapshot> findSnapshotById(@Param("id") Long id);

    /** Sum of the question scores of a test: its maximum score (2.4.3). */
    @RestResource(exported = false)
    @Query("SELECT COALESCE(SUM(q.score), 0) FROM Question q WHERE q.test.id = :testId")
    Long sumScoresByTestId(@Param("testId") Long testId);

    /** Number of options and of correct options of each question of a test (publication checks, 2.4.4). */
    interface QuestionOptionStats {
        Long getQuestionId();
        Long getOptionCount();
        Long getCorrectCount();
    }

    @RestResource(exported = false)
    @Query("SELECT q.id AS questionId, COUNT(o.id) AS optionCount, " +
           "COALESCE(SUM(CASE WHEN o.isCorrect = true THEN 1 ELSE 0 END), 0) AS correctCount " +
           "FROM Question q LEFT JOIN AnswerOption o ON o.question = q " +
           "WHERE q.test.id = :testId GROUP BY q.id ORDER BY q.id")
    List<QuestionOptionStats> listOptionStatsByTestId(@Param("testId") Long testId);

    /** All the questions of a test, without visibility filters (server-side grading). */
    @RestResource(exported = false)
    @Query("SELECT q FROM Question q WHERE q.test.id = :testId ORDER BY q.orderIndex, q.id")
    List<Question> listByTestId(@Param("testId") Long testId);
}
