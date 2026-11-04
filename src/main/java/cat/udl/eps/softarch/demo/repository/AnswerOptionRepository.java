package cat.udl.eps.softarch.demo.repository;

import cat.udl.eps.softarch.demo.domain.AnswerOption;
import cat.udl.eps.softarch.demo.domain.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource
public interface AnswerOptionRepository extends CrudRepository<AnswerOption, Long>, PagingAndSortingRepository<AnswerOption, Long> {

    String VISIBILITY_CLAUSE =
        "o.question.test.author.username = ?#{authentication.name} " +
        "OR (o.question.test.status = 'PUBLISHED' AND o.question.test.isPublic = true) " +
        "OR ?#{hasRole('ADMIN')} = true";

    @Override
    @Query("SELECT o FROM AnswerOption o WHERE " + VISIBILITY_CLAUSE)
    Page<AnswerOption> findAll(Pageable pageable);

    @Override
    @Query("SELECT o FROM AnswerOption o WHERE o.id = :id AND (" + VISIBILITY_CLAUSE + ")")
    Optional<AnswerOption> findById(@Param("id") Long id);

    @Query("SELECT o FROM AnswerOption o WHERE o.question = :question AND (" + VISIBILITY_CLAUSE + ") ORDER BY o.orderIndex")
    Page<AnswerOption> findByQuestion(@Param("question") Question question, Pageable pageable);
}
