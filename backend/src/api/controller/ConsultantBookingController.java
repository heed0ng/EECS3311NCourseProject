package backend.api.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import backend.api.dto.response.ActionResultResponse;
import backend.api.dto.response.ConsultantRequestSummaryResponse;
import backend.model.core.Booking;
import backend.service.ConsultantService;

@RestController
@RequestMapping("/api/consultant")
@CrossOrigin(origins = "*")
public class ConsultantBookingController {

    private final ConsultantService consultantService;

    public ConsultantBookingController(ConsultantService consultantService) {
        this.consultantService = consultantService;
    }

@GetMapping("/{consultantId}/booking-requests")
public ResponseEntity<?> getPendingBookingRequests(@PathVariable String consultantId) {
    List<ConsultantRequestSummaryResponse> responses = new ArrayList<>();

    try {
        List<Booking> bookings = this.consultantService.getPendingBookingRequests(consultantId);

        for (Booking currentBooking : bookings) {
            responses.add(toConsultantRequestSummaryResponse(currentBooking));
        }

        return ResponseEntity.ok(responses);

    } catch (Exception exception) {
        return ResponseEntity.badRequest().body(
                new ActionResultResponse(false, exception.getMessage()));
    }
}
    @PostMapping("/{consultantId}/booking-requests/{bookingId}/accept")
    public ResponseEntity<ActionResultResponse> acceptBookingRequest(
            @PathVariable String consultantId,
            @PathVariable String bookingId) {

        try {
            this.consultantService.acceptBookingRequest(consultantId, bookingId);

            return ResponseEntity.ok(
                    new ActionResultResponse(true, "Booking request accepted successfully."));

        } catch (Exception exception) {
            return ResponseEntity.badRequest().body(
                    new ActionResultResponse(false, exception.getMessage()));
        }
    }

    @PostMapping("/{consultantId}/booking-requests/{bookingId}/reject")
    public ResponseEntity<ActionResultResponse> rejectBookingRequest(
            @PathVariable String consultantId,
            @PathVariable String bookingId) {

        try {
            this.consultantService.rejectBookingRequest(consultantId, bookingId);

            return ResponseEntity.ok(
                    new ActionResultResponse(true, "Booking request rejected successfully."));

        } catch (Exception exception) {
            return ResponseEntity.badRequest().body(
                    new ActionResultResponse(false, exception.getMessage()));
        }
    }

    private ConsultantRequestSummaryResponse toConsultantRequestSummaryResponse(Booking booking) {
        return new ConsultantRequestSummaryResponse(
                booking.getBookingId(),
                booking.getClient().getUserId(),
                booking.getClient().getName(),
                booking.getOffering().getOfferingId(),
                booking.getOffering().getConsultingService().getName(),
                booking.getSlot().getSlotId(),
                booking.getSlot().getStartDateTime().toString(),
                booking.getSlot().getEndDateTime().toString(),
                booking.getStateName(),
                booking.getPrice());
    }
}