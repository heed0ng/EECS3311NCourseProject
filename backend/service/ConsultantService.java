package service;

import java.time.LocalDateTime;
import java.util.List;

import model.core.AvailabilitySlot;
import model.core.Booking;
import model.core.ConsultantServiceOffering;

public interface ConsultantService {
    AvailabilitySlot addAvailabilitySlot(String consultantId, LocalDateTime startDateTime, LocalDateTime endDateTime);
    ConsultantServiceOffering addServiceOffering(String consultantId, String serviceId, Double customPrice);
    List<AvailabilitySlot> getAvailabilitySlots(String consultantId);
    List<Booking> getPendingBookingRequests(String consultantId);
    Booking acceptBookingRequest(String consultantId, String bookingId);
    Booking rejectBookingRequest(String consultantId, String bookingId);
    Booking completeBooking(String consultantId, String bookingId);
}
