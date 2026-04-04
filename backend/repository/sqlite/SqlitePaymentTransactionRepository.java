package backend.repository.sqlite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import backend.model.payment.PaymentTransaction;
import backend.repository.PaymentTransactionRepository;
import backend.util.*;

public class SqlitePaymentTransactionRepository implements PaymentTransactionRepository {
    private final DatabaseManager databaseManager;
    private final SqliteBookingRepository bookingRepository;
    private final SqliteClientRepository clientRepository;

    public SqlitePaymentTransactionRepository(DatabaseManager databaseManager, SqliteBookingRepository bookingRepository, SqliteClientRepository clientRepository) {
        this.databaseManager = databaseManager;
        this.bookingRepository = bookingRepository;
        this.clientRepository = clientRepository;
    }

    @Override
    public List<PaymentTransaction> findByClient(String clientId) {
        return this.findMany("SELECT * FROM payment_transactions WHERE client_id = ? ORDER BY created_at DESC", clientId);
    }

    @Override
    public List<PaymentTransaction> findByBooking(String bookingId) {
        return this.findMany("SELECT * FROM payment_transactions WHERE booking_id = ? ORDER BY created_at DESC", bookingId);
    }

    @Override
    public void save(PaymentTransaction paymentTransaction) {
        String sql = "INSERT INTO payment_transactions(transaction_id, booking_id, client_id, transaction_type, status, method_type, amount, created_at) VALUES(?, ?, ?, ?, ?, ?, ?, ?) ON CONFLICT(transaction_id) DO UPDATE SET booking_id=excluded.booking_id, client_id=excluded.client_id, transaction_type=excluded.transaction_type, status=excluded.status, method_type=excluded.method_type, amount=excluded.amount, created_at=excluded.created_at";
        try (Connection c = databaseManager.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            s.setString(1, paymentTransaction.getTransactionId());
            s.setString(2, paymentTransaction.getBooking().getBookingId());
            s.setString(3, paymentTransaction.getClient().getUserId());
            s.setString(4, paymentTransaction.getTransactionType().name());
            s.setString(5, paymentTransaction.getStatus().name());
            s.setString(6, paymentTransaction.getMethodType().name());
            s.setDouble(7, paymentTransaction.getAmount());
            s.setString(8, paymentTransaction.getCreatedAt().toString());
            s.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Failed to save payment transaction.", e);
        }
    }

    private List<PaymentTransaction> findMany(String sql, String id) {
        try (Connection c = databaseManager.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            s.setString(1, id);
            try (ResultSet rs = s.executeQuery()) {
                List<PaymentTransaction> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(new PaymentTransaction(rs.getString("transaction_id"), bookingRepository.findById(rs.getString("booking_id")).orElseThrow(), clientRepository.findById(rs.getString("client_id")).orElseThrow(), PaymentTransactionType.valueOf(rs.getString("transaction_type")), PaymentTransactionStatus.valueOf(rs.getString("status")), PaymentMethodType.valueOf(rs.getString("method_type")), rs.getDouble("amount"), LocalDateTime.parse(rs.getString("created_at"))));
                }
                return list;
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to read payment transactions.", e);
        }
    }
}
