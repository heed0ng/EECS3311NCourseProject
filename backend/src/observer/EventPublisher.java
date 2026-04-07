package backend.observer;

import java.util.ArrayList;
import java.util.List;

import backend.model.notification.DomainEvent;

public class EventPublisher {
    private final List<Observer> observers;
    private final List<DomainEvent> publishedEvents;
    private int nextEventIdNumber = 1;

    public EventPublisher() {
        this.observers = new ArrayList<>();
        this.publishedEvents = new ArrayList<>();
    }

    public synchronized void subscribe(Observer observer) {
        if (observer != null
                && this.observers.stream().noneMatch(o -> o.getObserverId().equals(observer.getObserverId()))) {
            this.observers.add(observer);
        }
    }

    public synchronized void unsubscribe(Observer observer) {
        if (observer == null) {
            return;
        }

        this.observers.removeIf(o -> o.getObserverId().equals(observer.getObserverId()));
    }

    public void publish(DomainEvent event) {
        if (event == null) {
            return;
        }

        List<Observer> observerSnapshot;

        synchronized (this) {
            this.publishedEvents.add(event);
            observerSnapshot = new ArrayList<>(this.observers);
        }

        for (Observer observer : observerSnapshot) {
            if (observer.supports(event)) {
                observer.update(event);
            }
        }
    }

    public synchronized List<DomainEvent> getPublishedEvents() {
        return new ArrayList<>(this.publishedEvents);
    }

    public synchronized String nextEventId() {
        String eventId = "event-" + this.nextEventIdNumber;
        this.nextEventIdNumber++;
        return eventId;
    }
}