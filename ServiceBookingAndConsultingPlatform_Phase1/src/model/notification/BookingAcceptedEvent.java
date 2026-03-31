package model.notification;

import java.time.LocalDateTime;

public class BookingAcceptedEvent extends DomainEvent {
    public BookingAcceptedEvent(String eventId, LocalDateTime occurredAt, String message) {
        super(eventId, occurredAt, "BookingAccepted", message);
    }
}
