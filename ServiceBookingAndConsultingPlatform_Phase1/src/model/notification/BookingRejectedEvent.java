package model.notification;

import java.time.LocalDateTime;

public class BookingRejectedEvent extends DomainEvent {
    public BookingRejectedEvent(String eventId, LocalDateTime occurredAt, String message) {
        super(eventId, occurredAt, "BookingRejected", message);
    }
}
