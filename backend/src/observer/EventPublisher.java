package backend.observer;

import java.util.ArrayList;
import java.util.List;

import backend.model.notification.DomainEvent;

public class EventPublisher {

    private final List<Observer> observers;
    private int nextEventIdNumber = 1;

    public EventPublisher() {
        this.observers = new ArrayList<>();
    }

    public synchronized void subscribe(Observer observer) {
        if (observer != null && this.observers.stream().noneMatch(o -> o.getObserverId().equals(observer.getObserverId()))) {
            this.observers.add(observer);
        }
    }

    public synchronized void unsubscribe(Observer observer) {
        if (observer == null) return;
        this.observers.removeIf(o -> o.getObserverId().equals(observer.getObserverId()));
    }

    public void publish(DomainEvent event) {
        if (event == null) return;

        List<Observer> observerSnapshot;
        synchronized (this) {
            observerSnapshot = new ArrayList<>(this.observers);
        }

        for (Observer observer : observerSnapshot) {
            if (observer.supports(event)) observer.update(event);
        }
    }

    public synchronized String nextEventId() {
        String eventId = "event-" + this.nextEventIdNumber;
        this.nextEventIdNumber++;
        return eventId;
    }
}