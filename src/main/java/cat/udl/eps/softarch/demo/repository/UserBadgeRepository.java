package cat.udl.eps.softarch.demo.repository;

import cat.udl.eps.softarch.demo.domain.User;
import cat.udl.eps.softarch.demo.domain.UserBadge;
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

import java.util.Optional;

@RepositoryRestResource
public interface UserBadgeRepository extends CrudRepository<UserBadge, Long>, PagingAndSortingRepository<UserBadge, Long> {

    // Visible badges appear on public profiles
    String VISIBILITY_CLAUSE =
        "ub.user.username = ?#{authentication.name} " +
        "OR ub.isVisible = true " +
        "OR ?#{hasRole('ADMIN')} = true";

    @Override
    @Query("SELECT ub FROM UserBadge ub WHERE " + VISIBILITY_CLAUSE)
    Page<UserBadge> findAll(Pageable pageable);

    @Override
    @Query("SELECT ub FROM UserBadge ub WHERE ub.id = :id AND (" + VISIBILITY_CLAUSE + ")")
    Optional<UserBadge> findById(@Param("id") Long id);

    @Query("SELECT ub FROM UserBadge ub WHERE ub.user = :user AND (" + VISIBILITY_CLAUSE + ")")
    Page<UserBadge> findByUser(@Param("user") User user, Pageable pageable);

    /** Values as stored in the database (never the entity merged with the request body). */
    interface UserBadgeSnapshot {
        String getOwnerUsername();

        Long getBadgeId();
    }

    @RestResource(exported = false)
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    @Query("SELECT u.username AS ownerUsername, b.id AS badgeId " +
           "FROM UserBadge ub JOIN ub.user u JOIN ub.badge b WHERE ub.id = :id")
    Optional<UserBadgeSnapshot> findSnapshotById(@Param("id") Long id);

    /** Used by the server to award each badge at most once per user. */
    @RestResource(exported = false)
    @Query("SELECT COUNT(ub) FROM UserBadge ub WHERE ub.user.id = :userId AND ub.badge.id = :badgeId")
    long countOwned(@Param("userId") Long userId, @Param("badgeId") Long badgeId);
}
