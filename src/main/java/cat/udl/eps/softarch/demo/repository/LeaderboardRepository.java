package cat.udl.eps.softarch.demo.repository;

import cat.udl.eps.softarch.demo.domain.Leaderboard;
import cat.udl.eps.softarch.demo.domain.Subject;
import cat.udl.eps.softarch.demo.domain.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

// Leaderboards are public: anyone can read them, only admins modify them (see LeaderboardEventHandler)
@RepositoryRestResource
public interface LeaderboardRepository extends CrudRepository<Leaderboard, Long>, PagingAndSortingRepository<Leaderboard, Long> {

    @Override
    @Query("SELECT l FROM Leaderboard l WHERE l.user.role = 'STUDENT'")
    Page<Leaderboard> findAll(Pageable pageable);

    @Override
    @Query("SELECT l FROM Leaderboard l WHERE l.id = :id AND l.user.role = 'STUDENT'")
    Optional<Leaderboard> findById(@Param("id") Long id);

    @RestResource(exported = false)
    @Query("SELECT l FROM Leaderboard l")
    List<Leaderboard> listAllForRecalculation();

    @Query("SELECT l FROM Leaderboard l WHERE l.leaderboardType = :type AND l.user.role = 'STUDENT' ORDER BY l.rank ASC")
    List<Leaderboard> findByLeaderboardTypeOrderByRankAsc(@Param("type") Leaderboard.LeaderboardType type);

    @RestResource(path = "globalSummary", rel = "globalSummary")
    @Query("SELECT l FROM Leaderboard l JOIN l.user u WHERE l.leaderboardType = 'GLOBAL' " +
           "AND u.role = 'STUDENT' ORDER BY l.rank ASC, u.username ASC LIMIT 5")
    List<Leaderboard> findGlobalSummary();

    @RestResource(exported = false)
    @Query("SELECT l FROM Leaderboard l WHERE l.subject = :subject AND l.user.role = 'STUDENT' ORDER BY l.rank ASC, l.user.username ASC")
    List<Leaderboard> findBySubjectOrderByRankAsc(@Param("subject") Subject subject);

    @RestResource(path = "findBySubjectOrderByRankAsc", rel = "findBySubjectOrderByRankAsc")
    @Query("SELECT l FROM Leaderboard l WHERE l.subject = :subject AND l.user.role = 'STUDENT' ORDER BY l.rank ASC, l.user.username ASC")
    Page<Leaderboard> findSubjectLeaderboard(@Param("subject") Subject subject, Pageable pageable);

    // Internal use only (SubjectEventHandler): does the subject have any leaderboard entry?
    @RestResource(exported = false)
    boolean existsBySubject(Subject subject);

    @Query("SELECT l FROM Leaderboard l WHERE l.user = :user AND l.user.role = 'STUDENT'")
    List<Leaderboard> findByUser(@Param("user") User user);
}
