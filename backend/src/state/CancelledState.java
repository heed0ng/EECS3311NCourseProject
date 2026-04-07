package backend.state;

public class CancelledState extends AbstractBookingState {

    @Override
    public String getClientCancellationBlockedReason() {
        return "Booking is already cancelled.";
    }

    @Override
    public boolean isTerminal() {
        return true;
    }

    @Override
    public String getName() {
        return "Cancelled";
    }
}