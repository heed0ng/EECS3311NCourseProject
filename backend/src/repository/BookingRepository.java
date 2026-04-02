package repository;

import java.util.List;
import java.util.Optional;

import model.core.Booking;

public interface BookingRepository {
    Optional<Booking> findById(String bookingId);
    List<Booking> findByClient(String clientId);
    List<Booking> findByConsultant(String consultantId);
    List<Booking> findPendingRequestsForConsultant(String consultantId);
    List<Booking> findAll();
    void save(Booking booking);
}
