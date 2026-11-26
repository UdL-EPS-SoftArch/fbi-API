package cat.udl.eps.softarch.demo.repository;

import cat.udl.eps.softarch.demo.domain.Leaderboard.LeaderboardType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PublicProfile(
        @JsonIgnore Long userId,
        String username,
        boolean profilePublic,
        String profilePicture,
        String fullName,
        String email,
        List<EarnedBadge> badges,
        List<Position> leaderboardPositions,
        List<CustomTest> customTests) {

    public PublicProfile(Long userId, String username, Boolean profilePublic, String profilePicture,
            String fullName, String email) {
        this(userId, username, profilePublic, profilePicture, fullName, email, null, null, null);
    }

    public record EarnedBadge(String name, String iconUrl) {}

    public record Position(LeaderboardType leaderboardType, Integer rank, String subjectName) {}

    public record CustomTest(String title, String uri) {}
}
