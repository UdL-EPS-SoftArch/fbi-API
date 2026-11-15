package cat.udl.eps.softarch.demo.repository;

import cat.udl.eps.softarch.demo.domain.Leaderboard;
import cat.udl.eps.softarch.demo.domain.Subject;
import cat.udl.eps.softarch.demo.domain.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

// Leaderboards are public: anyone can read them, only admins modify them (see LeaderboardEventHandler)
@RepositoryRestResource
public interface LeaderboardRepository extends CrudRepository<Leaderboard, Long>, PagingAndSortingRepository<Leaderboard, Long> {

    List<Leaderboard> findByLeaderboardTypeOrderByRankAsc(@Param("type") Leaderboard.LeaderboardType type);

    List<Leaderboard> findBySubjectOrderByRankAsc(@Param("subject") Subject subject);

    List<Leaderboard> findByUser(@Param("user") User user);
}
