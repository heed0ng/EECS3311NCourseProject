package backend.paymentStrategy;

import backend.util.BusinessRuleViolationException;
import backend.util.PaymentMethodType;

public class PaymentStrategyFactory {
    public PaymentMethodStrategy create(PaymentMethodType paymentMethodType) {
        if (paymentMethodType == null) throw new BusinessRuleViolationException("Payment method type is required.");
        switch (paymentMethodType) {
            case CREDIT_CARD: return new CreditCardPaymentStrategy();
            case DEBIT_CARD: return new DebitCardPaymentStrategy();
            case PAYPAL: return new PayPalPaymentStrategy();
            case BANK_TRANSFER: return new BankTransferPaymentStrategy();
            default: throw new BusinessRuleViolationException("Unsupported payment method type.");
        }
    }
}
