package backend.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import backend.api.dto.request.RequestBookingRequest;
import backend.api.dto.response.ActionResultResponse;
import service.BookingService;

@RestController
@RequestMapping("/api/client")
@CrossOrigin(origins = "*")
public class ClientBookingController {

    // Replace Object with your real BookingService type
    private final Object bookingService;

    public ClientBookingController() {
        // TEMPORARY PLACEHOLDER
        // Replace with real construction or dependency injection
        this.bookingService = null;
    }

    @PostMapping("/bookings")
    public ResponseEntity<ActionResultResponse> requestBooking(
            @RequestBody RequestBookingRequest requestBookingRequest) {

        try {
 
            this.bookingService.requestBooking(requestBookingRequest.getClientId(),
                    requestBookingRequest.getOfferingId(),requestBookingRequest.getSlotId());

            ActionResultResponse response =  new ActionResultResponse(true, "Booking request submitted successfully.");

            return ResponseEntity.ok(response);

        } catch (Exception exception) {
            ActionResultResponse response =
                    new ActionResultResponse(false, exception.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }
}