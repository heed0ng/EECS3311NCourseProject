package backend.api.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import backend.api.dto.response.ActionResultResponse;
import backend.api.dto.response.AvailabilitySlotResponse;
import backend.api.dto.response.OfferingSummaryResponse;
import backend.api.mapper.AvailabilitySlotDtoMapper;
import backend.api.mapper.OfferingDtoMapper;
import backend.model.core.AvailabilitySlot;
import backend.model.core.ConsultantServiceOffering;
import backend.service.BookingService;

@RestController
@RequestMapping("/api/client")
@CrossOrigin(origins = "*")
public class ClientOfferingController {

    private final BookingService bookingService;

    public ClientOfferingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

@GetMapping("/offerings")
public ResponseEntity<?> getAvailableOfferings() {
    try {
        List<ConsultantServiceOffering> offerings = this.bookingService.browseAvailableOfferings();
        List<OfferingSummaryResponse> responses = new ArrayList<>();

        for (ConsultantServiceOffering currentOffering : offerings) {
            responses.add(OfferingDtoMapper.toOfferingSummaryResponse(currentOffering));
        }

        return ResponseEntity.ok(responses);

    } catch (Exception exception) {
        return ResponseEntity.badRequest().body(
                new ActionResultResponse(false, exception.getMessage()));
    }
}

@GetMapping("/offerings/{offeringId}/slots")
public ResponseEntity<?> getAvailableSlotsByOfferingId(@PathVariable String offeringId) {
    try {
        List<AvailabilitySlot> allAvailableSlots = this.bookingService.getAllAvailableSlots();
        List<AvailabilitySlotResponse> responses = new ArrayList<>();

        for (AvailabilitySlot currentSlot : allAvailableSlots) {
            if (currentSlot.getConsultant() != null
                    && currentSlot.getConsultant().getUserId() != null) {

                for (ConsultantServiceOffering currentOffering : this.bookingService.browseAvailableOfferings()) {
                    if (currentOffering.getOfferingId().equals(offeringId)
                            && currentOffering.getConsultant() != null
                            && currentSlot.getConsultant().getUserId()
                                    .equals(currentOffering.getConsultant().getUserId())) {

                        responses.add(
                                AvailabilitySlotDtoMapper.toAvailabilitySlotResponse(currentSlot, offeringId));
                    }
                }
            }
        }

        return ResponseEntity.ok(responses);

    } catch (Exception exception) {
        return ResponseEntity.badRequest().body(
                new ActionResultResponse(false, exception.getMessage()));
    }
}
}