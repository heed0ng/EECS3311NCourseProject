package backend.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import backend.api.dto.request.RequestBookingRequest;
import backend.api.dto.response.ActionResultResponse;
import backend.api.dto.response.BookingSummaryResponse;
import backend.api.dto.response.CancellationSummaryResponse;
import backend.api.mapper.BookingDtoMapper;

import backend.service.BookingService;
import backend.model.core.Booking;

@RestController
@RequestMapping("/api/client")
@CrossOrigin(origins = "*")
public class ClientBookingController {

    private final BookingService bookingService;

    public ClientBookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/bookings")
    public ResponseEntity<ActionResultResponse> requestBooking(@RequestBody RequestBookingRequest requestBookingRequest) {
        try {
            this.bookingService.requestBooking(
                    requestBookingRequest.getClientId(),
                    requestBookingRequest.getOfferingId(),
                    requestBookingRequest.getSlotId());

            return ResponseEntity.ok(new ActionResultResponse(true, "Booking request submitted successfully."));
        } catch (Exception exception) {
            return ResponseEntity.badRequest().body(new ActionResultResponse(false, exception.getMessage()));
        }
    }
    
@GetMapping("/{clientId}/bookings")
public ResponseEntity<?> getClientBookings(@PathVariable String clientId) {
    List<BookingSummaryResponse> responses = new ArrayList<>();

    try {
        List<Booking> bookings = this.bookingService.getBookingHistory(clientId);

        for (Booking currentBooking : bookings) {
            responses.add(BookingDtoMapper.toBookingSummaryResponse(currentBooking));
        }

        return ResponseEntity.ok(responses);

    } catch (Exception exception) {
        return ResponseEntity.badRequest().body(
                new ActionResultResponse(false, exception.getMessage()));
    }
}
    
    @GetMapping("/{clientId}/bookings/{bookingId}/cancellation-summary")
    public ResponseEntity<?> getCancellationSummary(
            @PathVariable String clientId,
            @PathVariable String bookingId) {
        try {
            String summaryMessage = this.bookingService.getCancellationSummary(clientId, bookingId);

            return ResponseEntity.ok(new CancellationSummaryResponse(bookingId, summaryMessage));
        } catch (Exception exception) {
            return ResponseEntity.badRequest().body(new ActionResultResponse(false, exception.getMessage()));
        }
    }
      
    @PostMapping("/{clientId}/bookings/{bookingId}/cancel")
    public ResponseEntity<ActionResultResponse> cancelBooking(@PathVariable String clientId, @PathVariable String bookingId) {
        try {
            this.bookingService.cancelBooking(clientId, bookingId);

            return ResponseEntity.ok(new ActionResultResponse(true, "Booking cancelled successfully."));
        } catch (Exception exception) {
            return ResponseEntity.badRequest().body(new ActionResultResponse(false, exception.getMessage()));
        }
    }
}