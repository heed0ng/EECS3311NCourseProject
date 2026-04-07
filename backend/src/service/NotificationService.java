package backend.service;

import java.util.List;

import backend.model.notification.UserNotification;

public interface NotificationService {
    List<UserNotification> getClientNotifications(String clientId);
    List<UserNotification> getConsultantNotifications(String consultantId);
    List<UserNotification> getAdminNotifications(String adminId);
}