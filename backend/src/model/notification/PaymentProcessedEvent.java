package backend.model.notification;

import java.time.LocalDateTime;

public class PaymentProcessedEvent extends DomainEvent {
    public PaymentProcessedEvent(String eventId, LocalDateTime occurredAt, String message) {
        super(eventId, occurredAt, "PaymentProcessed", message);
    }

    public PaymentProcessedEvent(
            String eventId,
            LocalDateTime occurredAt,
            String message,
            String clientId,
            String consultantId,
            String adminId) {
        super(eventId, occurredAt, "PaymentProcessed", message, clientId, consultantId, adminId);
    }
}