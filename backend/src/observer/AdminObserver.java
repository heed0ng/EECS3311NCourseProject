package backend.observer;

import backend.model.notification.DomainEvent;
import backend.model.notification.UserNotification;
import backend.repository.NotificationRepository;

public class AdminObserver implements Observer {

    private final String observerId;
    private final String adminId;
    private final String name;
    private final NotificationRepository notificationRepository;

    public AdminObserver(
        String observerId,
        String adminId,
        String name,
        NotificationRepository notificationRepository
    ) {
        this.observerId = observerId;
        this.adminId = adminId;
        this.name = name;
        this.notificationRepository = notificationRepository;
    }

    @Override
    public void update(DomainEvent event) {
        if (event.getAdminId() != null && !event.getAdminId().equals(this.adminId)) return;

        this.notificationRepository.save(new UserNotification(event.getEventId(), this.adminId, "ADMIN", event.getEventType(), 
        		event.getMessage(), event.getOccurredAt()) );
    }

    @Override
    public String getObserverId() {
        return this.observerId;
    }

    @Override
    public boolean supports(DomainEvent event) {
        return "ConsultantApproval".equals(event.getEventType()) || "PolicyUpdated".equals(event.getEventType());
    }
}