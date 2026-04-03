package state;

import model.core.Booking;

public class PendingPaymentState extends AbstractBookingState {
    @Override
    public void markPaid(Booking booking) { booking.setState(new PaidState()); }
    @Override
    public void cancel(Booking booking) { booking.setState(new CancelledState()); }
    @Override
    public String getName() { return "Pending Payment"; }
}
