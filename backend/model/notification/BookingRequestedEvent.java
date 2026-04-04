package backend.model.notification;

import java.time.LocalDateTime;

public class BookingRequestedEvent extends DomainEvent {
    public BookingRequestedEvent(String eventId, LocalDateTime occurredAt, String message) {
        super(eventId, occurredAt, "BookingRequested", message);
    }
}
