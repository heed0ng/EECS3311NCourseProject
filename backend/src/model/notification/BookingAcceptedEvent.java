package backend.model.notification;

import java.time.LocalDateTime;

public class BookingAcceptedEvent extends DomainEvent {
    public BookingAcceptedEvent(String eventId, LocalDateTime occurredAt, String message) {
        super(eventId, occurredAt, "BookingAccepted", message);
    }

    public BookingAcceptedEvent(
            String eventId,
            LocalDateTime occurredAt,
            String message,
            String clientId,
            String consultantId,
            String adminId) {
        super(eventId, occurredAt, "BookingAccepted", message, clientId, consultantId, adminId);
    }
}