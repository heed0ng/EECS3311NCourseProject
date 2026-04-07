package backend.api.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import backend.api.dto.request.CreateAvailabilitySlotRequest;
import backend.api.dto.request.UpdateAvailabilitySlotRequest;
import backend.api.dto.response.ActionResultResponse;
import backend.api.dto.response.AvailabilitySlotResponse;
import backend.api.mapper.AvailabilitySlotDtoMapper;
import backend.model.core.AvailabilitySlot;
import backend.service.ConsultantService;

@RestController
@RequestMapping("/api/consultant")
@CrossOrigin(origins = "*")
public class ConsultantAvailabilityController {

    private final ConsultantService consultantService;

    public ConsultantAvailabilityController(ConsultantService consultantService) {
        this.consultantService = consultantService;
    }

@GetMapping("/{consultantId}/availability")
public ResponseEntity<?> getAvailabilitySlots(@PathVariable String consultantId) {
    List<AvailabilitySlotResponse> responses = new ArrayList<>();

    try {
        List<AvailabilitySlot> availabilitySlots =
                this.consultantService.getAvailabilitySlots(consultantId);

        for (AvailabilitySlot currentSlot : availabilitySlots) {
            responses.add(
                    AvailabilitySlotDtoMapper.toAvailabilitySlotResponse(
                            currentSlot,
                            ""));
        }

        return ResponseEntity.ok(responses);

    } catch (Exception exception) {
        return ResponseEntity.badRequest().body(
                new ActionResultResponse(false, exception.getMessage()));
    }
}

    @PostMapping("/{consultantId}/availability")
    public ResponseEntity<?> createAvailabilitySlot(
            @PathVariable String consultantId,
            @RequestBody CreateAvailabilitySlotRequest createAvailabilitySlotRequest) {

        try {
            LocalDateTime startDateTime =
                    LocalDateTime.parse(createAvailabilitySlotRequest.getStartDateTime());

            LocalDateTime endDateTime =
                    LocalDateTime.parse(createAvailabilitySlotRequest.getEndDateTime());

            AvailabilitySlot createdSlot = this.consultantService.addAvailabilitySlot(
                    consultantId,
                    startDateTime,
                    endDateTime);

            AvailabilitySlotResponse response =
                    AvailabilitySlotDtoMapper.toAvailabilitySlotResponse(createdSlot, "");

            return ResponseEntity.ok(response);

        } catch (Exception exception) {
            return ResponseEntity.badRequest().body(
                    new ActionResultResponse(false, exception.getMessage()));
        }
    }
    
    @DeleteMapping("/{consultantId}/availability/{slotId}")
    public ResponseEntity<?> removeAvailabilitySlot(
            @PathVariable String consultantId,
            @PathVariable String slotId) {

        try {
            this.consultantService.removeAvailabilitySlot(consultantId, slotId);

            return ResponseEntity.ok(new ActionResultResponse(true, "Availability slot removed from active availability successfully."));

        } catch (Exception exception) {
            return ResponseEntity.badRequest().body(new ActionResultResponse(false, exception.getMessage()));
        }
    }
    
    @PutMapping("/{consultantId}/availability/{slotId}")
    public ResponseEntity<?> updateAvailabilitySlot(
            @PathVariable String consultantId,
            @PathVariable String slotId,
            @RequestBody UpdateAvailabilitySlotRequest updateAvailabilitySlotRequest) {

        try {
            LocalDateTime startDateTime =
                    LocalDateTime.parse(updateAvailabilitySlotRequest.getStartDateTime());

            LocalDateTime endDateTime =
                    LocalDateTime.parse(updateAvailabilitySlotRequest.getEndDateTime());

            AvailabilitySlot updatedSlot = this.consultantService.updateAvailabilitySlot(
                    consultantId,
                    slotId,
                    startDateTime,
                    endDateTime);

            AvailabilitySlotResponse response =
                    AvailabilitySlotDtoMapper.toAvailabilitySlotResponse(updatedSlot, "");

            return ResponseEntity.ok(response);

        } catch (Exception exception) {
            return ResponseEntity.badRequest().body(
                    new ActionResultResponse(false, exception.getMessage()));
        }
    }
    
}