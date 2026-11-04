package cat.udl.eps.softarch.demo.handler;

import cat.udl.eps.softarch.demo.domain.Notification;
import cat.udl.eps.softarch.demo.domain.User;
import cat.udl.eps.softarch.demo.repository.NotificationRepository;
import org.springframework.data.rest.core.annotation.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RepositoryEventHandler
public class NotificationEventHandler {
    final NotificationRepository notificationRepository;

    public NotificationEventHandler(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @HandleBeforeCreate
    public void handleNotificationPreCreate(Notification notification) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user.getRole() != User.Role.ADMIN) {
            throw new AccessDeniedException("Only administrators can send notifications");
        }
    }

    @HandleBeforeSave
    public void handleNotificationPreSave(Notification notification) {
        checkRecipientOrAdmin(notification);
    }

    @HandleBeforeDelete
    public void handleNotificationPreDelete(Notification notification) {
        checkRecipientOrAdmin(notification);
    }

    private void checkRecipientOrAdmin(Notification notification) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user.getRole() != User.Role.ADMIN
                && !user.getUsername().equals(notification.getRecipient().getUsername())) {
            throw new AccessDeniedException("Only the recipient can modify the notification");
        }
    }
}
