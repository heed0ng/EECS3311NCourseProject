package backend.model.notification;

import java.time.LocalDateTime;

public class ConsultantApprovalEvent extends DomainEvent {
    public ConsultantApprovalEvent(String eventId, LocalDateTime occurredAt, String message) {
        super(eventId, occurredAt, "ConsultantApproval", message);
    }
}
