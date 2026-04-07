package backend.state;

import backend.model.core.Booking;
import backend.util.BusinessRuleViolationException;

public abstract class AbstractBookingState implements BookingState {

    @Override
    public void confirm(Booking booking) {
        throw invalidTransition("confirm");
    }

    @Override
    public void moveToPendingPayment(Booking booking) {
        throw invalidTransition("move to pending payment");
    }

    @Override
    public void markPaid(Booking booking) {
        throw invalidTransition("mark paid");
    }

    @Override
    public void reject(Booking booking) {
        throw invalidTransition("reject");
    }

    @Override
    public void cancel(Booking booking) {
        throw invalidTransition("cancel");
    }

    @Override
    public void complete(Booking booking) {
        throw invalidTransition("complete");
    }

    @Override
    public boolean canClientCancel() {
        return false;
    }

    @Override
    public String getClientCancellationBlockedReason() {
        return "Cancellation is not available when booking is in state " + getName() + ".";
    }

    @Override
    public boolean isTerminal() {
        return false;
    }

    protected BusinessRuleViolationException invalidTransition(String action) {
        return new BusinessRuleViolationException(
            "Cannot " + action + " when booking is in state " + getName() + "."
        );
    }
}