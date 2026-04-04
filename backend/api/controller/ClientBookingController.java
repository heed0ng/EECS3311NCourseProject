package backend.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import backend.api.dto.request.RequestBookingRequest;
import backend.api.dto.response.ActionResultResponse;
import backend.service.BookingService;

@RestController
@RequestMapping("/api/client")
@CrossOrigin(origins = "*")
public class ClientBookingController {

    private final BookingService bookingService;

    public ClientBookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/bookings")
    public ResponseEntity<ActionResultResponse> requestBooking(
            @RequestBody RequestBookingRequest requestBookingRequest) {

        try {
            this.bookingService.requestBooking(
                    requestBookingRequest.getClientId(),
                    requestBookingRequest.getOfferingId(),
                    requestBookingRequest.getSlotId());

            return ResponseEntity.ok(
                    new ActionResultResponse(true, "Booking request submitted successfully."));
        } catch (Exception exception) {
            return ResponseEntity.badRequest().body(
                    new ActionResultResponse(false, exception.getMessage()));
        }
    }
}