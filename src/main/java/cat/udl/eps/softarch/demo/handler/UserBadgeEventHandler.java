package cat.udl.eps.softarch.demo.handler;

import cat.udl.eps.softarch.demo.domain.User;
import cat.udl.eps.softarch.demo.domain.UserBadge;
import cat.udl.eps.softarch.demo.repository.UserBadgeRepository;
import org.springframework.data.rest.core.annotation.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.Instant;

// Badges are awarded by the server; through the API, users can only show or hide their own
@Component
@RepositoryEventHandler
public class UserBadgeEventHandler {
    final UserBadgeRepository userBadgeRepository;

    public UserBadgeEventHandler(UserBadgeRepository userBadgeRepository) {
        this.userBadgeRepository = userBadgeRepository;
    }

    @HandleBeforeCreate
    public void handleUserBadgePreCreate(UserBadge userBadge) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user.getRole() != User.Role.ADMIN) {
            throw new AccessDeniedException("Badges cannot be self-assigned");
        }
        userBadge.setEarnedAt(Instant.now());
    }

    @HandleBeforeSave
    public void handleUserBadgePreSave(UserBadge userBadge) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user.getRole() != User.Role.ADMIN
                && !user.getUsername().equals(userBadge.getUser().getUsername())) {
            throw new AccessDeniedException("Only the owner can change the badge visibility");
        }
    }

    @HandleBeforeDelete
    public void handleUserBadgePreDelete(UserBadge userBadge) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user.getRole() != User.Role.ADMIN) {
            throw new AccessDeniedException("Only administrators can remove earned badges");
        }
    }
}
