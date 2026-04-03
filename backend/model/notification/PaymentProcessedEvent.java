package model.notification;

import java.time.LocalDateTime;

public class PaymentProcessedEvent extends DomainEvent {
    public PaymentProcessedEvent(String eventId, LocalDateTime occurredAt, String message) {
        super(eventId, occurredAt, "PaymentProcessed", message);
    }
}
