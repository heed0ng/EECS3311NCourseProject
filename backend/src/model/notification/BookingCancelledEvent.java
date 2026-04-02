package model.notification;

import java.time.LocalDateTime;

public class BookingCancelledEvent extends DomainEvent {
    public BookingCancelledEvent(String eventId, LocalDateTime occurredAt, String message) {
        super(eventId, occurredAt, "BookingCancelled", message);
    }
}
