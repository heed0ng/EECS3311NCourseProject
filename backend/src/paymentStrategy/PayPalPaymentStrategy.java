package backend.paymentStrategy;

import backend.model.core.Booking;
import backend.model.payment.SavedPaymentMethod;
import backend.util.BusinessRuleViolationException;
import backend.util.PaymentMethodType;

public class PayPalPaymentStrategy implements PaymentMethodStrategy {
    @Override
    public PaymentMethodType getMethodType() { return PaymentMethodType.PAYPAL; }

    @Override
    public void validate(SavedPaymentMethod method) {
        String email = method.getPaymentDetails() == null ? "" : method.getPaymentDetails().trim();
        if (!email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) throw new BusinessRuleViolationException("PayPal email format is invalid.");
    }
    
    @Override
    public String process(Booking booking, SavedPaymentMethod method) {
        return "Processed simulated PayPal payment for booking " + booking.getBookingId();
    }
}
