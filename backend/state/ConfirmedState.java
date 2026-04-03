package state;

import model.core.Booking;

public class ConfirmedState extends AbstractBookingState {
    @Override
    public void moveToPendingPayment(Booking booking) { booking.setState(new PendingPaymentState()); }
    @Override
    public void cancel(Booking booking) { booking.setState(new CancelledState()); }
    @Override
    public String getName() { return "Confirmed"; }
}
