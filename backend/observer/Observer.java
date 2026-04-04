package backend.observer;

import backend.model.notification.DomainEvent;

public interface Observer {
    void update(DomainEvent event);
    boolean supports(DomainEvent event);
    String getObserverId();
}
