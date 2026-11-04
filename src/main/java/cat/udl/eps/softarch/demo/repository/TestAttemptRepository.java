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
}
