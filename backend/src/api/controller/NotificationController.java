package backend.api.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import backend.api.dto.response.ActionResultResponse;
import backend.api.dto.response.NotificationEventResponse;
import backend.model.notification.UserNotification;
import backend.repository.AdminRepository;
import backend.repository.ClientRepository;
import backend.repository.ConsultantRepository;
import backend.service.NotificationService;
import backend.util.EntityNotFoundException;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class NotificationController {

    private final NotificationService notificationService;
    private final ClientRepository clientRepository;
    private final ConsultantRepository consultantRepository;
    private final AdminRepository adminRepository;

    public NotificationController(NotificationService notificationService, ClientRepository clientRepository,
        ConsultantRepository consultantRepository,AdminRepository adminRepository) {
        this.notificationService = notificationService;
        this.clientRepository = clientRepository;
        this.consultantRepository = consultantRepository;
        this.adminRepository = adminRepository;
    }

    @GetMapping("/client/{clientId}/notifications")
    public ResponseEntity<?> getClientNotifications(@PathVariable String clientId) {
        try {
            this.clientRepository.findById(clientId).orElseThrow(() -> new EntityNotFoundException("Client not found."));

            return ResponseEntity.ok(this.toResponses(this.notificationService.getClientNotifications(clientId)));
        } catch (Exception exception) {
            return ResponseEntity.badRequest().body(new ActionResultResponse(false, exception.getMessage()));
        }
    }

    @GetMapping("/consultant/{consultantId}/notifications")
    public ResponseEntity<?> getConsultantNotifications(@PathVariable String consultantId) {
        try {
            this.consultantRepository.findById(consultantId).orElseThrow(() -> new EntityNotFoundException("Consultant not found."));

            return ResponseEntity.ok(this.toResponses(this.notificationService.getConsultantNotifications(consultantId))
            );
        } catch (Exception exception) {
            return ResponseEntity.badRequest().body(new ActionResultResponse(false, exception.getMessage()));
        }
    }

    @GetMapping("/admin/{adminId}/notifications")
    public ResponseEntity<?> getAdminNotifications(@PathVariable String adminId) {
        try {
            this.adminRepository.findById(adminId).orElseThrow(() -> new EntityNotFoundException("Admin not found."));

            return ResponseEntity.ok(this.toResponses(this.notificationService.getAdminNotifications(adminId)));
        } catch (Exception exception) {
            return ResponseEntity.badRequest().body(new ActionResultResponse(false, exception.getMessage()));
        }
    }

    private List<NotificationEventResponse> toResponses(List<UserNotification> notifications) {
        return notifications.stream().map(notification -> new NotificationEventResponse(notification.getSourceEventId(),
                notification.getOccurredAt().toString(), notification.getEventType(), notification.getMessage())).collect(Collectors.toList());
    }
}