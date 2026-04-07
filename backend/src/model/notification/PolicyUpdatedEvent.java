package backend.model.notification;

import java.time.LocalDateTime;

public class PolicyUpdatedEvent extends DomainEvent {
    public PolicyUpdatedEvent(String eventId, LocalDateTime occurredAt, String message) {
        super(eventId, occurredAt, "PolicyUpdated", message);
    }

    public PolicyUpdatedEvent(String eventId, LocalDateTime occurredAt, String message, String clientId, String consultantId, String adminId) {
        super(eventId, occurredAt, "PolicyUpdated", message, clientId, consultantId, adminId);
    }
}