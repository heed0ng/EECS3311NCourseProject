package backend.api.dto.response;

public class NotificationEventResponse {

    private String eventId;
    private String occurredAt;
    private String eventType;
    private String message;

    public NotificationEventResponse() {
    }

    public NotificationEventResponse(
            String eventId,
            String occurredAt,
            String eventType,
            String message) {
        this.eventId = eventId;
        this.occurredAt = occurredAt;
        this.eventType = eventType;
        this.message = message;
    }

    public String getEventId() {
        return this.eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getOccurredAt() {
        return this.occurredAt;
    }

    public void setOccurredAt(String occurredAt) {
        this.occurredAt = occurredAt;
    }

    public String getEventType() {
        return this.eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}