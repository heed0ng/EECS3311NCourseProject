package backend.api.mapper;

import backend.api.dto.response.PaymentTransactionResponse;
import backend.api.dto.response.SavedPaymentMethodResponse;
import backend.model.payment.PaymentTransaction;
import backend.model.payment.SavedPaymentMethod;

public final class PaymentDtoMapper {

    private PaymentDtoMapper() {
    }

    public static SavedPaymentMethodResponse toSavedPaymentMethodResponse(
            SavedPaymentMethod savedPaymentMethod) {

        return new SavedPaymentMethodResponse(
                savedPaymentMethod.getSavedMethodId(),
                savedPaymentMethod.getMethodType().toString(),
                savedPaymentMethod.getDisplayLabel(),
                savedPaymentMethod.getMaskedPaymentDetails()
        );
    }

    public static PaymentTransactionResponse toPaymentTransactionResponse(
            PaymentTransaction paymentTransaction,
            String message) {

        return new PaymentTransactionResponse(
                paymentTransaction.getTransactionId(),
                paymentTransaction.getBooking().getBookingId(),
                paymentTransaction.getMethodType().toString(),
                paymentTransaction.getAmount(),
                paymentTransaction.getStatus().toString(),
                paymentTransaction.getTransactionType().toString(),
                paymentTransaction.getCreatedAt().toString(),
                message
        );
    }
}