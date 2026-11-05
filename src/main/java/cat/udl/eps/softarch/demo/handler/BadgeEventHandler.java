package cat.udl.eps.softarch.demo.handler;

import cat.udl.eps.softarch.demo.domain.Badge;
import cat.udl.eps.softarch.demo.domain.User;
import cat.udl.eps.softarch.demo.repository.BadgeRepository;
import org.springframework.data.rest.core.annotation.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RepositoryEventHandler
public class BadgeEventHandler {
    final BadgeRepository badgeRepository;

    public BadgeEventHandler(BadgeRepository badgeRepository) {
        this.badgeRepository = badgeRepository;
    }

    @HandleBeforeCreate
    public void handleBadgePreCreate(Badge badge) {
        checkAdmin();
    }

    @HandleBeforeSave
    public void handleBadgePreSave(Badge badge) {
        checkAdmin();
    }

    @HandleBeforeDelete
    public void handleBadgePreDelete(Badge badge) {
        checkAdmin();
    }

    private void checkAdmin() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user.getRole() != User.Role.ADMIN) {
            throw new AccessDeniedException("Only administrators can manage badges");
        }
    }
}
