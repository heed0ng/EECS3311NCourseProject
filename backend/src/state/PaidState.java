package backend.state;

import backend.model.core.Booking;

public class PaidState extends AbstractBookingState {
	@Override
    public void cancel(Booking booking) { booking.setState(new CancelledState()); }
    @Override
    public void complete(Booking booking) { booking.setState(new CompletedState()); }
    @Override
    public String getName() { return "Paid"; }
}
