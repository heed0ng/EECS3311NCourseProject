package observer;

import model.notification.DomainEvent;

public interface Observer {
    void update(DomainEvent event);
    boolean supports(DomainEvent event);
    String getObserverId();
}
