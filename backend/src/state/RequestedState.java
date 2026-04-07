package backend.state;

import backend.model.core.Booking;

public class RequestedState extends AbstractBookingState {

    @Override
    public void confirm(Booking booking) {
        booking.setState(new ConfirmedState());
    }

    @Override
    public void reject(Booking booking) {
        booking.setState(new RejectedState());
    }

    @Override
    public void cancel(Booking booking) {
        booking.setState(new CancelledState());
    }

    @Override
    public boolean canClientCancel() {
        return true;
    }

    @Override
    public String getClientCancellationBlockedReason() {
        return null;
    }

    @Override
    public String getName() {
        return "Requested";
    }
}