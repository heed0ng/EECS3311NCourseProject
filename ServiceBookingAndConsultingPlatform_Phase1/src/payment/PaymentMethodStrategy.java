package payment;

import model.core.Booking;
import model.payment.SavedPaymentMethod;
import util.PaymentMethodType;

public interface PaymentMethodStrategy {
    PaymentMethodType getMethodType();
    void validate(SavedPaymentMethod savedPaymentMethod);
    String process(Booking booking, SavedPaymentMethod savedPaymentMethod);
}
