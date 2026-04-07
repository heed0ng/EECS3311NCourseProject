package backend.paymentStrategy;

import backend.model.core.Booking;
import backend.model.payment.SavedPaymentMethod;
import backend.util.BusinessRuleViolationException;
import backend.util.PaymentMethodType;

public class BankTransferPaymentStrategy implements PaymentMethodStrategy {
    @Override
    public PaymentMethodType getMethodType() { return PaymentMethodType.BANK_TRANSFER; }

    @Override
    public void validate(SavedPaymentMethod method) {
        String accountNumber = method.getPaymentDetails() == null ? "" : method.getPaymentDetails().replaceAll("\\D", "");
        String routingNumber = method.getPaymentDetailData() == null ? "" : method.getPaymentDetailData().replaceAll("\\D", "");
        if (!accountNumber.matches("\\d{6,17}")) throw new BusinessRuleViolationException("Bank account number must contain 6 to 17 digits.");
        if (!routingNumber.matches("\\d{9}")) throw new BusinessRuleViolationException("Routing number must contain exactly 9 digits.");
    }
    
    @Override
    public String process(Booking booking, SavedPaymentMethod method) {
        return "Processed simulated bank transfer payment for booking " + booking.getBookingId();
    }
}
