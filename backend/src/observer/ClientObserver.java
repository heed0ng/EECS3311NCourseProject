package backend.observer;

import backend.model.notification.DomainEvent;
import backend.model.notification.UserNotification;
import backend.repository.NotificationRepository;

public class ClientObserver implements Observer {

    private final String observerId;
    private final String clientId;
    private final String name;
    private final NotificationRepository notificationRepository;

    public ClientObserver(String observerId, String clientId, String name, NotificationRepository notificationRepository) {
        this.observerId = observerId;
        this.clientId = clientId;
        this.name = name;
        this.notificationRepository = notificationRepository;
    }

    @Override
    public void update(DomainEvent event) {
        if (event.getClientId() != null && !event.getClientId().equals(this.clientId)) return;

        this.notificationRepository.save(new UserNotification(event.getEventId(), this.clientId, "CLIENT",
        		event.getEventType(), event.getMessage(), event.getOccurredAt()));
    }

    @Override
    public String getObserverId() {
        return this.observerId;
    }

    @Override
    public boolean supports(DomainEvent event) {
        return "BookingAccepted".equals(event.getEventType())
            || "BookingRejected".equals(event.getEventType())
            || "BookingCancelled".equals(event.getEventType())
            || "PaymentProcessed".equals(event.getEventType())
            || "BookingRequested".equals(event.getEventType());
    }
}