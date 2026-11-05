package cat.udl.eps.softarch.demo.handler;

import cat.udl.eps.softarch.demo.domain.User;
import cat.udl.eps.softarch.demo.domain.UserBadge;
import cat.udl.eps.softarch.demo.repository.UserBadgeRepository;
import org.springframework.data.rest.core.annotation.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

// Badges are awarded only by the server (BadgeService). Through the API the owner can only
// show or hide an earned badge; nobody, administrators included, can create or reassign one.
@Component
@RepositoryEventHandler
public class UserBadgeEventHandler {
    final UserBadgeRepository userBadgeRepository;

    public UserBadgeEventHandler(UserBadgeRepository userBadgeRepository) {
        this.userBadgeRepository = userBadgeRepository;
    }

    @HandleBeforeCreate
    public void handleUserBadgePreCreate(UserBadge userBadge) {
        throw new AccessDeniedException("Badges are awarded automatically by the server");
    }

    @HandleBeforeSave
    public void handleUserBadgePreSave(UserBadge userBadge) {
        User user = CurrentUser.get();

        // The incoming entity is already merged with the request body: compare with the stored row
        UserBadgeRepository.UserBadgeSnapshot stored = userBadgeRepository.findSnapshotById(userBadge.getId())
                .orElseThrow(() -> new AccessDeniedException("Badge not found"));

        if (!user.getUsername().equals(stored.getOwnerUsername())) {
            throw new AccessDeniedException("Only the owner can change the badge visibility");
        }
        // Only isVisible may change: neither the user nor the badge can be rewritten
        if (!stored.getOwnerUsername().equals(CurrentUser.usernameOf(userBadge.getUser()))
                || userBadge.getBadge() == null
                || !stored.getBadgeId().equals(userBadge.getBadge().getId())) {
            throw new AccessDeniedException("Only the visibility of an earned badge can be changed");
        }
    }

    @HandleBeforeDelete
    public void handleUserBadgePreDelete(UserBadge userBadge) {
        User user = CurrentUser.get();
        if (!CurrentUser.isAdmin(user)) {
            throw new AccessDeniedException("Only administrators can remove earned badges");
        }
    }
}
