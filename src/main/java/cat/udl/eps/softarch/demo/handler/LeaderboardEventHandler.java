package cat.udl.eps.softarch.demo.handler;

import cat.udl.eps.softarch.demo.domain.Leaderboard;
import cat.udl.eps.softarch.demo.domain.User;
import cat.udl.eps.softarch.demo.repository.LeaderboardRepository;
import org.springframework.data.rest.core.annotation.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.Instant;

// Leaderboard entries are derived data, normally recalculated by the server, not edited by hand
@Component
@RepositoryEventHandler
public class LeaderboardEventHandler {
    final LeaderboardRepository leaderboardRepository;

    public LeaderboardEventHandler(LeaderboardRepository leaderboardRepository) {
        this.leaderboardRepository = leaderboardRepository;
    }

    @HandleBeforeCreate
    public void handleLeaderboardPreCreate(Leaderboard leaderboard) {
        checkAdmin();
        leaderboard.setCalculatedAt(Instant.now());
    }

    @HandleBeforeSave
    public void handleLeaderboardPreSave(Leaderboard leaderboard) {
        checkAdmin();
        leaderboard.setCalculatedAt(Instant.now());
    }

    @HandleBeforeDelete
    public void handleLeaderboardPreDelete(Leaderboard leaderboard) {
        checkAdmin();
    }

    private void checkAdmin() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user.getRole() != User.Role.ADMIN) {
            throw new AccessDeniedException("Only administrators can manage leaderboards");
        }
    }
}
