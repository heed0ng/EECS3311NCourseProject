package observer;

import java.util.ArrayList;
import java.util.List;

import model.notification.DomainEvent;

public class EventPublisher {
    private final List<Observer> observers;
    private int nextEventIdNumber = 1;

    public EventPublisher() {
    	this.observers = new ArrayList<>();
    }
    
    public void subscribe(Observer observer) {
        if (observer != null && this.observers.stream().noneMatch(o -> o.getObserverId().equals(observer.getObserverId()))) {
            this.observers.add(observer);
        }
    }

    public void unsubscribe(Observer observer) {
        if (observer == null) return;
        this.observers.removeIf(o -> o.getObserverId().equals(observer.getObserverId()));
    }

    public void publish(DomainEvent event) {
        for (Observer observer : observers) {
        	if (observer.supports(event)) observer.update(event);
        }
    }
    
    public synchronized String nextEventId() {
        String eventId = "event-" + this.nextEventIdNumber;
        this.nextEventIdNumber++;
        return eventId;
    }
}
