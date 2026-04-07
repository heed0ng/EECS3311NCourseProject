package backend.model.notification;

import java.time.LocalDateTime;

public class UserNotification {

    private final long notificationId;
    private final String sourceEventId;
    private final String recipientUserId;
    private final String recipientRole;
    private final String eventType;
    private final String message;
    private final LocalDateTime occurredAt;
    private final boolean read;

    public UserNotification(long notificationId, String sourceEventId, String recipientUserId, String recipientRole,
        String eventType, String message, LocalDateTime occurredAt, boolean read) {
        this.notificationId = notificationId;
        this.sourceEventId = sourceEventId;
        this.recipientUserId = recipientUserId;
        this.recipientRole = recipientRole;
        this.eventType = eventType;
        this.message = message;
        this.occurredAt = occurredAt;
        this.read = read;
    }

    public UserNotification(String sourceEventId, String recipientUserId, String recipientRole, String eventType,
        String message, LocalDateTime occurredAt) {
        this(0L, sourceEventId, recipientUserId, recipientRole, eventType, message, occurredAt, false);
    }

    public long getNotificationId() {
        return this.notificationId;
    }

    public String getSourceEventId() {
        return this.sourceEventId;
    }

    public String getRecipientUserId() {
        return this.recipientUserId;
    }

    public String getRecipientRole() {
        return this.recipientRole;
    }

    public String getEventType() {
        return this.eventType;
    }

    public String getMessage() {
        return this.message;
    }

    public LocalDateTime getOccurredAt() {
        return this.occurredAt;
    }

    public boolean isRead() {
        return this.read;
    }
}