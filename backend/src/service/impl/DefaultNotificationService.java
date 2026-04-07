package backend.service.impl;

import java.util.List;

import backend.model.notification.UserNotification;
import backend.repository.NotificationRepository;
import backend.service.NotificationService;

public class DefaultNotificationService implements NotificationService {

    private static final String CLIENT_ROLE = "CLIENT";
    private static final String CONSULTANT_ROLE = "CONSULTANT";
    private static final String ADMIN_ROLE = "ADMIN";

    private final NotificationRepository notificationRepository;

    public DefaultNotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public List<UserNotification> getClientNotifications(String clientId) {
        return this.notificationRepository.findByRecipient(CLIENT_ROLE, clientId);
    }

    @Override
    public List<UserNotification> getConsultantNotifications(String consultantId) {
        return this.notificationRepository.findByRecipient(CONSULTANT_ROLE, consultantId);
    }

    @Override
    public List<UserNotification> getAdminNotifications(String adminId) {
        return this.notificationRepository.findByRecipient(ADMIN_ROLE, adminId);
    }
}