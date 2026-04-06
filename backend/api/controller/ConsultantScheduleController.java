package backend.api.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import backend.api.dto.response.ActionResultResponse;
import backend.api.dto.response.ConsultantScheduleEntryResponse;
import backend.model.core.Booking;
import backend.repository.BookingRepository;
import backend.service.ConsultantService;

@RestController
@RequestMapping("/api/consultant")
@CrossOrigin(origins = "*")
public class ConsultantScheduleController {

    private final ConsultantService consultantService;
    private final BookingRepository bookingRepository;

    public ConsultantScheduleController(
            ConsultantService consultantService,
            BookingRepository bookingRepository) {
        this.consultantService = consultantService;
        this.bookingRepository = bookingRepository;
    }

@GetMapping("/{consultantId}/schedule")
public ResponseEntity<?> getConsultantSchedule(@PathVariable String consultantId) {
    List<ConsultantScheduleEntryResponse> responses = new ArrayList<>();

    try {
        List<Booking> bookings = this.bookingRepository.findByConsultant(consultantId);

        for (Booking currentBooking : bookings) {
            responses.add(toConsultantScheduleEntryResponse(currentBooking));
        }

        return ResponseEntity.ok(responses);

    } catch (Exception exception) {
        return ResponseEntity.badRequest().body(
                new ActionResultResponse(false, exception.getMessage()));
    }
}

    @PostMapping("/{consultantId}/schedule/{bookingId}/complete")
    public ResponseEntity<ActionResultResponse> completeBooking(
            @PathVariable String consultantId,
            @PathVariable String bookingId) {

        try {
            this.consultantService.completeBooking(consultantId, bookingId);

            return ResponseEntity.ok(
                    new ActionResultResponse(true, "Booking completed successfully."));

        } catch (Exception exception) {
            return ResponseEntity.badRequest().body(
                    new ActionResultResponse(false, exception.getMessage()));
        }
    }

    private ConsultantScheduleEntryResponse toConsultantScheduleEntryResponse(Booking booking) {
        return new ConsultantScheduleEntryResponse(
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