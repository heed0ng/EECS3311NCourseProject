package payment;

import model.core.Booking;
import model.payment.SavedPaymentMethod;
import util.BusinessRuleViolationException;
import util.PaymentMethodType;

public class CreditCardPaymentStrategy implements PaymentMethodStrategy {
    @Override
    public PaymentMethodType getMethodType() { return PaymentMethodType.CREDIT_CARD; }
    @Override
    public void validate(SavedPaymentMethod method) {
        String cardNumber = method.getPaymentDetails() == null ? "" : method.getPaymentDetails().replaceAll("\\D", "");
        String metadata = method.getPaymentDetailData() == null ? "" : method.getPaymentDetailData().trim();

        if (!cardNumber.matches("\\d{16}")) throw new BusinessRuleViolationException("Credit card number must contain exactly 16 digits.");
        if (!metadata.contains("|")) throw new BusinessRuleViolationException("Credit card metadata must be MM/YY|CVV.");

        String[] parts = metadata.split("\\|");
        if (parts.length != 2) throw new BusinessRuleViolationException("Credit card metadata must be MM/YY|CVV.");

        String expiry = parts[0].trim();
        String cvv = parts[1].trim();
        
        if (!expiry.matches("\\d{2}/\\d{2}")) throw new BusinessRuleViolationException("Credit card expiry must be MM/YY.");
        if (!cvv.matches("\\d{3,4}")) throw new BusinessRuleViolationException("Credit card CVV must contain 3 or 4 digits.");

        try {
            java.time.YearMonth expiryMY = java.time.YearMonth.parse(expiry, java.time.format.DateTimeFormatter.ofPattern("MM/yy"));
            if (expiryMY.isBefore(java.time.YearMonth.now())) throw new BusinessRuleViolationException("Credit card is expired.");
        } catch (java.time.format.DateTimeParseException exception) {
            throw new BusinessRuleViolationException("Credit card expiry format is invalid.");
        }
    }
    
    @Override
    public String process(Booking booking, SavedPaymentMethod method) {
        return "Processed simulated credit card payment for booking " + booking.getBookingId();
    }
}
