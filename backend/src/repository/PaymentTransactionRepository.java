package repository;

import java.util.List;

import model.payment.PaymentTransaction;

public interface PaymentTransactionRepository {
    List<PaymentTransaction> findByClient(String clientId);
    List<PaymentTransaction> findByBooking(String bookingId);
    void save(PaymentTransaction paymentTransaction);
}
