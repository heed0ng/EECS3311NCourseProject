package backend.api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import backend.api.dto.response.SystemStatusResponse;
import backend.model.core.Booking;
import backend.repository.BookingRepository;
import backend.repository.ConsultantRepository;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminStatusController {

    private final ConsultantRepository consultantRepository;
    private final BookingRepository bookingRepository;

    public AdminStatusController(
            ConsultantRepository consultantRepository,
            BookingRepository bookingRepository) {
        this.consultantRepository = consultantRepository;
        this.bookingRepository = bookingRepository;
    }

    @GetMapping("/status")
    public ResponseEntity<SystemStatusResponse> getSystemStatus() {
        try {
            int pendingConsultantCount = this.consultantRepository.findPendingApproval().size();

            List<Booking> allBookings = this.bookingRepository.findAll();

            int requestedBookingCount = 0;
            int pendingPaymentCount = 0;
            int paidBookingCount = 0;
            int completedBookingCount = 0;

            for (Booking currentBooking : allBookings) {
                String currentStateName = currentBooking.getStateName();

                if ("Requested".equals(currentStateName)) {
                    requestedBookingCount++;
                } else if ("Pending Payment".equals(currentStateName)) {
                    pendingPaymentCount++;
                } else if ("Paid".equals(currentStateName)) {
                    paidBookingCount++;
                } else if ("Completed".equals(currentStateName)) {
                    completedBookingCount++;
                }
            }

            SystemStatusResponse response = new SystemStatusResponse(
                    pendingConsultantCount,
                    requestedBookingCount,
                    pendingPaymentCount,
                    paidBookingCount,
                    completedBookingCount,
                    null
            );

            return ResponseEntity.ok(response);

        } catch (Exception exception) {
            return ResponseEntity.badRequest().body(new SystemStatusResponse());
        }
    }
}