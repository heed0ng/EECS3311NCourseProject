package state;

import model.core.Booking;

public interface BookingState {
    void confirm(Booking booking);
    void moveToPendingPayment(Booking booking);
    void markPaid(Booking booking);
    void reject(Booking booking);
    void cancel(Booking booking);
    void complete(Booking booking);
    String getName();
}
