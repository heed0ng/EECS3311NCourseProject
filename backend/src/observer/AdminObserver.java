package observer;

import model.notification.DomainEvent;

public class AdminObserver implements Observer {
    private final String observerId;
    private final String name;

    public AdminObserver(String observerId, String name) {
        this.observerId = observerId;
        this.name = name;
    }

    @Override
    public void update(DomainEvent event) {
        System.out.println("[Notification][Admin: " + this.name + "] " + event.getMessage());
    }

    @Override
    public String getObserverId() {
        return this.observerId;
    }

	@Override
	public boolean supports(DomainEvent event) {
		return "ConsultantApproval".equals(event.getEventType());
	}
}
