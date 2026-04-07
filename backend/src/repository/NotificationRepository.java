package backend.repository;

import java.util.List;
import backend.model.notification.UserNotification;

public interface NotificationRepository {
    void save(UserNotification notification);
    List<UserNotification> findByRecipient(String recipientRole, String recipientUserId);
}