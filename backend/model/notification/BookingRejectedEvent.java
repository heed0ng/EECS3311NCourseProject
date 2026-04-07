package backend.model.notification;

import java.time.LocalDateTime;

public class BookingRejectedEvent extends DomainEvent {
    public BookingRejectedEvent(String eventId, LocalDateTime occurredAt, String message) {
        super(eventId, occurredAt, "BookingRejected", message);
    }

    public BookingRejectedEvent(
            String eventId,
            LocalDateTime occurredAt,
            String message,
            String clientId,
            String consultantId,
            String adminId) {
        super(eventId, occurredAt, "BookingRejected", message, clientId, consultantId, adminId);
    }
}