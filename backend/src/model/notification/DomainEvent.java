package backend.model.notification;

import java.time.LocalDateTime;

public abstract class DomainEvent {
    private final String eventId;
    private final LocalDateTime occurredAt;
    private final String eventType;
    private final String message;
    private final String clientId;
    private final String consultantId;
    private final String adminId;

    public DomainEvent(String eventId, LocalDateTime occurredAt, String eventType, String message) {
        this(eventId, occurredAt, eventType, message, null, null, null);
    }

    public DomainEvent(
            String eventId,
            LocalDateTime occurredAt,
            String eventType,
            String message,
            String clientId,
            String consultantId,
            String adminId) {
        this.eventId = eventId;
        this.occurredAt = occurredAt;
        this.eventType = eventType;
        this.message = message;
        this.clientId = clientId;
        this.consultantId = consultantId;
        this.adminId = adminId;
    }

    public String getEventId() { return this.eventId; }
    public LocalDateTime getOccurredAt() { return this.occurredAt; }
    public String getEventType() { return this.eventType; }
    public String getMessage() { return this.message; }
    public String getClientId() { return this.clientId; }
    public String getConsultantId() { return this.consultantId; }
    public String getAdminId() { return this.adminId; }
}