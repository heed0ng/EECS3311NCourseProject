package observer;

import model.notification.DomainEvent;

public class ClientObserver implements Observer {
    private final String observerId;
    private final String name;

    public ClientObserver(String observerId, String name) {
        this.observerId = observerId;
        this.name = name;
    }

    @Override
    public void update(DomainEvent event) {
        System.out.println("[Notification][Client Observer: " + this.name + "] " + "Accepted event type '" + event.getEventType() + "': " + event.getMessage());
    }

    @Override
    public String getObserverId() {
        return this.observerId;
    }

	@Override
	public boolean supports(DomainEvent event) {
		return "BookingAccepted".equals(event.getEventType())
		        || "BookingRejected".equals(event.getEventType())
		        || "BookingCancelled".equals(event.getEventType())
		        || "PaymentProcessed".equals(event.getEventType())
		        || "BookingRequested".equals(event.getEventType());
	}
}
