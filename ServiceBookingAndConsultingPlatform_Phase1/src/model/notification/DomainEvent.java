package model.notification;

import java.time.LocalDateTime;

public abstract class DomainEvent {
    private final String eventId;
    private final LocalDateTime occurredAt;
    private final String eventType;
    private final String message;

    protected DomainEvent(String eventId, LocalDateTime occurredAt, String eventType, String message) {
        this.eventId = eventId;
        this.occurredAt = occurredAt;
        this.eventType = eventType;
        this.message = message;
    }

    public String getEventId() { return this.eventId; }
    public LocalDateTime getOccurredAt() { return this.occurredAt; }
    public String getEventType() { return this.eventType; }
    public String getMessage() { return this.message; }
}
