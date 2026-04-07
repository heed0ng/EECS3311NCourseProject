package backend.api.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import backend.api.dto.response.ActionResultResponse;
import backend.api.dto.response.NotificationEventResponse;
import backend.model.notification.DomainEvent;
import backend.observer.EventPublisher;
import backend.repository.AdminRepository;
import backend.repository.ClientRepository;
import backend.repository.ConsultantRepository;
import backend.util.EntityNotFoundException;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class NotificationController {

    private final EventPublisher eventPublisher;
    private final ClientRepository clientRepository;
    private final ConsultantRepository consultantRepository;
    private final AdminRepository adminRepository;

    public NotificationController(
            EventPublisher eventPublisher,
            ClientRepository clientRepository,
            ConsultantRepository consultantRepository,
            AdminRepository adminRepository) {
        this.eventPublisher = eventPublisher;
        this.clientRepository = clientRepository;
        this.consultantRepository = consultantRepository;
        this.adminRepository = adminRepository;
    }

    @GetMapping("/client/{clientId}/notifications")
    public ResponseEntity<?> getClientNotifications(@PathVariable String clientId) {
        try {
            this.clientRepository.findById(clientId)
                    .orElseThrow(() -> new EntityNotFoundException("Client not found."));

            return ResponseEntity.ok(this.buildResponses(event -> this.supportsClient(clientId, event)));

        } catch (Exception exception) {
            return ResponseEntity.badRequest().body(
                    new ActionResultResponse(false, exception.getMessage()));
        }
    }

    @GetMapping("/consultant/{consultantId}/notifications")
    public ResponseEntity<?> getConsultantNotifications(@PathVariable String consultantId) {
        try {
            this.consultantRepository.findById(consultantId)
                    .orElseThrow(() -> new EntityNotFoundException("Consultant not found."));

            return ResponseEntity.ok(this.buildResponses(event -> this.supportsConsultant(consultantId, event)));

        } catch (Exception exception) {
            return ResponseEntity.badRequest().body(
                    new ActionResultResponse(false, exception.getMessage()));
        }
    }

    @GetMapping("/admin/{adminId}/notifications")
    public ResponseEntity<?> getAdminNotifications(@PathVariable String adminId) {
        try {
            this.adminRepository.findById(adminId)
                    .orElseThrow(() -> new EntityNotFoundException("Admin not found."));

            return ResponseEntity.ok(this.buildResponses(event -> this.supportsAdmin(adminId, event)));

        } catch (Exception exception) {
            return ResponseEntity.badRequest().body(
                    new ActionResultResponse(false, exception.getMessage()));
        }
    }

    private List<NotificationEventResponse> buildResponses(Predicate<DomainEvent> predicate) {
        List<DomainEvent> publishedEvents = this.eventPublisher.getPublishedEvents();
        List<NotificationEventResponse> responses = new ArrayList<>();

        for (int index = publishedEvents.size() - 1; index >= 0; index--) {
            DomainEvent currentEvent = publishedEvents.get(index);

            if (predicate.test(currentEvent)) {
                responses.add(new NotificationEventResponse(
                        currentEvent.getEventId(),
                        currentEvent.getOccurredAt().toString(),
                        currentEvent.getEventType(),
                        currentEvent.getMessage()));
            }
        }

        return responses;
    }

    private boolean supportsClient(String clientId, DomainEvent event) {
        String eventType = event.getEventType();

        if (!("BookingAccepted".equals(eventType)
                || "BookingRejected".equals(eventType)
                || "BookingCancelled".equals(eventType)
                || "PaymentProcessed".equals(eventType)
                || "BookingRequested".equals(eventType))) {
            return false;
        }

        return event.getClientId() == null || event.getClientId().equals(clientId);
    }

    private boolean supportsConsultant(String consultantId, DomainEvent event) {
        String eventType = event.getEventType();

        if (!("BookingRequested".equals(eventType)
                || "BookingCancelled".equals(eventType)
                || "PaymentProcessed".equals(eventType)
                || "PolicyUpdated".equals(eventType))) {
            return false;
        }

        return event.getConsultantId() == null || event.getConsultantId().equals(consultantId);
    }

    private boolean supportsAdmin(String adminId, DomainEvent event) {
        String eventType = event.getEventType();

        if (!("ConsultantApproval".equals(eventType) || "PolicyUpdated".equals(eventType))) {
            return false;
        }

        return event.getAdminId() == null || event.getAdminId().equals(adminId);
    }
}