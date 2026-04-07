package backend.api.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import backend.api.dto.request.CreateServiceOfferingRequest;
import backend.api.dto.response.ActionResultResponse;
import backend.api.dto.response.ConsultingServiceCatalogResponse;
import backend.api.dto.response.OfferingSummaryResponse;
import backend.api.mapper.OfferingDtoMapper;
import backend.model.core.ConsultantServiceOffering;
import backend.model.core.ConsultingService;
import backend.repository.ConsultantServiceOfferingRepository;
import backend.repository.ConsultingServiceRepository;
import backend.service.ConsultantService;

@RestController
@RequestMapping("/api/consultant")
@CrossOrigin(origins = "*")
public class ConsultantOfferingController {

    private final ConsultantService consultantService;
    private final ConsultingServiceRepository consultingServiceRepository;
    private final ConsultantServiceOfferingRepository offeringRepository;

    public ConsultantOfferingController(
            ConsultantService consultantService,
            ConsultingServiceRepository consultingServiceRepository,
            ConsultantServiceOfferingRepository offeringRepository) {
        this.consultantService = consultantService;
        this.consultingServiceRepository = consultingServiceRepository;
        this.offeringRepository = offeringRepository;
    }

@GetMapping("/services")
public ResponseEntity<?> getActiveConsultingServices() {
    List<ConsultingServiceCatalogResponse> responses = new ArrayList<>();

    try {
        List<ConsultingService> services = this.consultingServiceRepository.findAllActive();

        for (ConsultingService currentService : services) {
            responses.add(new ConsultingServiceCatalogResponse(
                    currentService.getServiceId(),
                    currentService.getName(),
                    currentService.getDescription(),
                    currentService.getDurationMinutes(),
                    currentService.getBasePrice()));
        }

        return ResponseEntity.ok(responses);

    } catch (Exception exception) {
        return ResponseEntity.badRequest().body(
                new ActionResultResponse(false, exception.getMessage()));
    }
}

@GetMapping("/{consultantId}/offerings")
public ResponseEntity<?> getConsultantOfferings(@PathVariable String consultantId) {
    List<OfferingSummaryResponse> responses = new ArrayList<>();

    try {
        List<ConsultantServiceOffering> offerings = this.offeringRepository.findByConsultant(consultantId);

        for (ConsultantServiceOffering currentOffering : offerings) {
            responses.add(OfferingDtoMapper.toOfferingSummaryResponse(currentOffering));
        }

        return ResponseEntity.ok(responses);

    } catch (Exception exception) {
        return ResponseEntity.badRequest().body(
                new ActionResultResponse(false, exception.getMessage()));
    }
}

    @PostMapping("/{consultantId}/offerings")
    public ResponseEntity<?> createServiceOffering(
            @PathVariable String consultantId,
            @RequestBody CreateServiceOfferingRequest createServiceOfferingRequest) {

        try {
            ConsultantServiceOffering createdOffering = this.consultantService.addServiceOffering(
                    consultantId,
                    createServiceOfferingRequest.getServiceId(),
                    createServiceOfferingRequest.getCustomPrice());

            return ResponseEntity.ok(
                    OfferingDtoMapper.toOfferingSummaryResponse(createdOffering));

        } catch (Exception exception) {
            return ResponseEntity.badRequest().body(
                    new ActionResultResponse(false, exception.getMessage()));
        }
    }
    
    @DeleteMapping("/{consultantId}/offerings/{offeringId}")
    public ResponseEntity<?> removeServiceOffering(
            @PathVariable String consultantId,
            @PathVariable String offeringId) {

        try {
            this.consultantService.removeServiceOffering(consultantId, offeringId);

            return ResponseEntity.ok(
                    new ActionResultResponse(true, "Service offering removed from active listings successfully."));

        } catch (Exception exception) {
            return ResponseEntity.badRequest().body(new ActionResultResponse(false, exception.getMessage()));
        }
    }
    
}
