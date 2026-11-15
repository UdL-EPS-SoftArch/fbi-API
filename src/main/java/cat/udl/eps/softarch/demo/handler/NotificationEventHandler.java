package cat.udl.eps.softarch.demo.handler;

import cat.udl.eps.softarch.demo.domain.Notification;
import cat.udl.eps.softarch.demo.domain.User;
import cat.udl.eps.softarch.demo.repository.NotificationRepository;
import org.springframework.data.rest.core.annotation.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RepositoryEventHandler
public class NotificationEventHandler {
    final NotificationRepository notificationRepository;

    public NotificationEventHandler(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @HandleBeforeCreate
    public void handleNotificationPreCreate(Notification notification) {
        if (!CurrentUser.isAdmin(CurrentUser.get())) {
            throw new AccessDeniedException("Only administrators can send notifications");
        }
    }

    @HandleBeforeSave
    public void handleNotificationPreSave(Notification notification) {
        User user = CurrentUser.get();

        // The incoming entity is already merged with the request body: compare with the stored row
        NotificationRepository.NotificationSnapshot stored = notificationRepository.findSnapshotById(notification.getId())
                .orElseThrow(() -> new AccessDeniedException("Notification not found"));

        if (CurrentUser.isAdmin(user)) {
            return;
        }
        if (!user.getUsername().equals(stored.getRecipientUsername())) {
            throw new AccessDeniedException("Only the recipient can modify the notification");
        }
        // The recipient can only mark it as seen: not reassign it nor rewrite the message
        if (!stored.getRecipientUsername().equals(CurrentUser.usernameOf(notification.getRecipient()))
                || !Objects.equals(stored.getMessage(), notification.getMessage())) {
            throw new AccessDeniedException("Only the seen flag of a notification can be changed");
        }
    }

    @HandleBeforeDelete
    public void handleNotificationPreDelete(Notification notification) {
        User user = CurrentUser.get();
        if (!CurrentUser.isAdmin(user)
                && !user.getUsername().equals(CurrentUser.usernameOf(notification.getRecipient()))) {
            throw new AccessDeniedException("Only the recipient can modify the notification");
        }
    }
}
