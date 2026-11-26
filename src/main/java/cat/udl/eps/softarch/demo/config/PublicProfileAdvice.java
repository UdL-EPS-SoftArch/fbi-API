package cat.udl.eps.softarch.demo.config;

import cat.udl.eps.softarch.demo.repository.LeaderboardRepository;
import cat.udl.eps.softarch.demo.repository.PublicProfile;
import cat.udl.eps.softarch.demo.repository.TestRepository;
import cat.udl.eps.softarch.demo.repository.UserBadgeRepository;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice
public class PublicProfileAdvice implements ResponseBodyAdvice<Object> {
    private final UserBadgeRepository badges;
    private final LeaderboardRepository leaderboards;
    private final TestRepository tests;

    public PublicProfileAdvice(UserBadgeRepository badges, LeaderboardRepository leaderboards, TestRepository tests) {
        this.badges = badges;
        this.leaderboards = leaderboards;
        this.tests = tests;
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType contentType,
            Class<? extends HttpMessageConverter<?>> converterType,
            ServerHttpRequest request, ServerHttpResponse response) {
        if (!(body instanceof PublicProfile profile) || !profile.profilePublic()) {
            return body;
        }

        var earnedBadges = badges.findVisibleProfileBadges(profile.userId()).stream()
                .map(badge -> new PublicProfile.EarnedBadge(badge.getName(), badge.getIconUrl()))
                .toList();
        var positions = leaderboards.findPublicPositionsForProfile(profile.userId()).stream()
                .map(entry -> new PublicProfile.Position(entry.getLeaderboardType(), entry.getRank(),
                        entry.getSubject() == null ? null : entry.getSubject().getName()))
                .toList();
        var customTests = tests.findPublicCustomTestsForProfile(profile.userId()).stream()
                .map(test -> new PublicProfile.CustomTest(test.getTitle(), test.getUri()))
                .toList();

        return new PublicProfile(profile.userId(), profile.username(), true, profile.profilePicture(),
                profile.fullName(), profile.email(), earnedBadges, positions, customTests);
    }
}
