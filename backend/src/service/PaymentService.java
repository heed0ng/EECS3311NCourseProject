package service;

import java.util.List;

import model.payment.PaymentTransaction;
import model.payment.SavedPaymentMethod;
import util.PaymentMethodType;

public interface PaymentService {
    SavedPaymentMethod addSavedPaymentMethod(String clientId, PaymentMethodType methodType, String displayLabel,
            String paymentDetails, String paymentDetailData);
    SavedPaymentMethod updateSavedPaymentMethod(String clientId, String savedMethodId, String displayLabel,
    		String paymentDetails, String paymentDetailData);
    void removeSavedPaymentMethod(String clientId, String savedMethodId);
    List<SavedPaymentMethod> getSavedPaymentMethods(String clientId);
    PaymentTransaction processPayment(String clientId, String bookingId, String savedMethodId);
    List<PaymentTransaction> getPaymentHistory(String clientId);
}
