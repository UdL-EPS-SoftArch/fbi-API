package cat.udl.eps.softarch.demo.config;

import cat.udl.eps.softarch.demo.domain.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("leaderboardPrivacy")
public class LeaderboardPrivacy {
    public boolean anonymousForViewer(User user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return user.isAnonymousInLeaderboards()
                && (authentication == null || !user.getUsername().equals(authentication.getName()));
    }

    public String displayName(User user) {
        return anonymousForViewer(user) ? "Anonymous student" : user.getUsername();
    }
}
