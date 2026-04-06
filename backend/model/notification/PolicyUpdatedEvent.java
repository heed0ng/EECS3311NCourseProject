package backend.model.notification;

import java.time.LocalDateTime;

public class PolicyUpdatedEvent extends DomainEvent {
    public PolicyUpdatedEvent(String eventId, LocalDateTime occurredAt, String message) {
        super(eventId, occurredAt, "PolicyUpdated", message);
    }
}