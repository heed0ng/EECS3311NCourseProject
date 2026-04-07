package backend.model.notification;

import java.time.LocalDateTime;

public class BookingCancelledEvent extends DomainEvent {
    public BookingCancelledEvent(String eventId, LocalDateTime occurredAt, String message) {
        super(eventId, occurredAt, "BookingCancelled", message);
    }

    public BookingCancelledEvent(
            String eventId,
            LocalDateTime occurredAt,
            String message,
            String clientId,
            String consultantId,
            String adminId) {
        super(eventId, occurredAt, "BookingCancelled", message, clientId, consultantId, adminId);
    }
}