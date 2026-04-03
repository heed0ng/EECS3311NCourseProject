package backend.api.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import backend.api.dto.response.AvailabilitySlotResponse;
import backend.api.dto.response.OfferingSummaryResponse;
import backend.api.mapper.AvailabilitySlotDtoMapper;
import backend.api.mapper.OfferingDtoMapper;

import service.BookingService;
import service.ConsultantService;
import model.core.ConsultantServiceOffering;
import model.core.AvailabilitySlot;

@RestController
@RequestMapping("/api/client")
@CrossOrigin(origins = "*")
public class ClientOfferingController {

    // Replace with your real service type(s)
    private final ConsultantService consultantService;
    private final BookingService bookingService;

    public ClientOfferingController() {
        // TEMPORARY PLACEHOLDER
        // Replace with your real service construction or dependency injection
        this.consultantService = null;
        this.bookingService = null;
    }

    @GetMapping("/offerings")
    public ResponseEntity<List<OfferingSummaryResponse>> getAvailableOfferings() {
        List<OfferingSummaryResponse> responses = new ArrayList<>();
        List<ConsultantServiceOffering> offerings = this.consultantService.getAvailableOfferings();

        for (ConsultantServiceOffering currentOffering : offerings) {
            responses.add(OfferingDtoMapper.toOfferingSummaryResponse(currentOffering));
        }
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/offerings/{offeringId}/slots")
    public ResponseEntity<List<AvailabilitySlotResponse>> getAvailableSlotsByOfferingId(
            @PathVariable String offeringId) {

        List<AvailabilitySlotResponse> responses = new ArrayList<>();

        List<AvailabilitySlot> slots = this.bookingService.getAvailableSlotsForOffering(offeringId);

        for (AvailabilitySlot currentSlot : slots) {
            responses.add(AvailabilitySlotDtoMapper.toAvailabilitySlotResponse(currentSlot));
        }

        return ResponseEntity.ok(responses);
    }
}