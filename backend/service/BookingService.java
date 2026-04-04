package backend.service;

import java.util.List;

import backend.model.core.*;

public interface BookingService {
    List<ConsultantServiceOffering> browseAvailableOfferings();
    Booking requestBooking(String clientId, String offeringId, String slotId);
    Booking cancelBooking(String clientId, String bookingId);
    List<Booking> getBookingHistory(String clientId);
    List<Booking> getAllBookings();
    List<AvailabilitySlot> getAllAvailableSlots();
    String getCancellationSummary(String clientId, String bookingId);
}
