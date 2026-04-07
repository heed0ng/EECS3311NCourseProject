package backend.state;

public class CompletedState extends AbstractBookingState {

    @Override
    public String getClientCancellationBlockedReason() {
        return "Booking is already completed and can no longer be cancelled.";
    }

    @Override
    public boolean isTerminal() {
        return true;
    }

    @Override
    public String getName() {
        return "Completed";
    }
}