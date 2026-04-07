package backend.model.notification;

import java.time.LocalDateTime;

public class BookingRequestedEvent extends DomainEvent {
    public BookingRequestedEvent(String eventId, LocalDateTime occurredAt, String message) {
        super(eventId, occurredAt, "BookingRequested", message);
    }

    public BookingRequestedEvent(
            String eventId,
            LocalDateTime occurredAt,
            String message,
            String clientId,
            String consultantId,
            String adminId) {
        super(eventId, occurredAt, "BookingRequested", message, clientId, consultantId, adminId);
    }
}