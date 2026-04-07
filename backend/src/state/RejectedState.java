package backend.state;

public class RejectedState extends AbstractBookingState {

    @Override
    public String getClientCancellationBlockedReason() {
        return "Booking was already rejected by the consultant.";
    }

    @Override
    public boolean isTerminal() {
        return true;
    }

    @Override
    public String getName() {
        return "Rejected";
    }
}