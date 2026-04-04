package backend.paymentStrategy;

import backend.model.core.Booking;
import backend.model.payment.SavedPaymentMethod;
import backend.util.PaymentMethodType;

public interface PaymentMethodStrategy {
    PaymentMethodType getMethodType();
    void validate(SavedPaymentMethod savedPaymentMethod);
    String process(Booking booking, SavedPaymentMethod savedPaymentMethod);
}
