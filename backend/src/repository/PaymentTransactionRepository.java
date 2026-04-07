package backend.repository;

import java.util.List;

import backend.model.payment.PaymentTransaction;

public interface PaymentTransactionRepository {
    List<PaymentTransaction> findByClient(String clientId);
    List<PaymentTransaction> findByBooking(String bookingId);
    void save(PaymentTransaction paymentTransaction);
}
