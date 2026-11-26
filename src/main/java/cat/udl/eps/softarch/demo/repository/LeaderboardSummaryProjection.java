package cat.udl.eps.softarch.demo.repository;

import cat.udl.eps.softarch.demo.domain.Leaderboard;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

@Projection(name = "leaderboardSummary", types = Leaderboard.class)
public interface LeaderboardSummaryProjection {
    @Value("#{target.user.role.name() == 'ADMIN' ? null : (target.user.leaderboardAnonymous == true ? 'Anonymous student' : target.user.username)}")
    String getUsername();

    Integer getRank();
    Integer getScore();
}
