package backend.observer;

import backend.model.notification.DomainEvent;

public class ConsultantObserver implements Observer {
    private final String observerId;
    private final String name;

    public ConsultantObserver(String observerId, String name) {
        this.observerId = observerId;
        this.name = name;
    }

    @Override
    public void update(DomainEvent event) {
        System.out.println("[Console Notification Demo][Consultant Observer: " + this.name + "] " + "Accepted event type '" + event.getEventType() + "': " + event.getMessage());
    }

    @Override
    public String getObserverId() {
        return this.observerId;
    }

    @Override
    public boolean supports(DomainEvent event) {
        return "BookingRequested".equals(event.getEventType())
                || "BookingCancelled".equals(event.getEventType())
                || "PaymentProcessed".equals(event.getEventType());
    }
}
