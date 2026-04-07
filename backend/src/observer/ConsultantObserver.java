package backend.observer;

import backend.model.notification.DomainEvent;
import backend.model.notification.UserNotification;
import backend.repository.NotificationRepository;

public class ConsultantObserver implements Observer {

    private final String observerId;
    private final String consultantId;
    private final String name;
    private final NotificationRepository notificationRepository;

    public ConsultantObserver(
        String observerId,
        String consultantId,
        String name,
        NotificationRepository notificationRepository
    ) {
        this.observerId = observerId;
        this.consultantId = consultantId;
        this.name = name;
        this.notificationRepository = notificationRepository;
    }

    @Override
    public void update(DomainEvent event) {
        if (event.getConsultantId() != null && !event.getConsultantId().equals(this.consultantId)) {
            return;
        }

        this.notificationRepository.save(new UserNotification(event.getEventId(), this.consultantId, "CONSULTANT",
                event.getEventType(), event.getMessage(), event.getOccurredAt()));
    }

    @Override
    public String getObserverId() {
        return this.observerId;
    }

    @Override
    public boolean supports(DomainEvent event) {
        return "BookingRequested".equals(event.getEventType())
            || "BookingCancelled".equals(event.getEventType())
            || "PaymentProcessed".equals(event.getEventType())
            || "PolicyUpdated".equals(event.getEventType());
    }
}