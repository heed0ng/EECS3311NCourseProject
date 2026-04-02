package repository.sqlite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import model.payment.SavedPaymentMethod;
import model.user.Client;
import repository.SavedPaymentMethodRepository;
import util.PaymentMethodType;

public class SqliteSavedPaymentMethodRepository implements SavedPaymentMethodRepository {
    private final DatabaseManager databaseManager;
    private final SqliteClientRepository clientRepository;

    public SqliteSavedPaymentMethodRepository(DatabaseManager databaseManager, SqliteClientRepository clientRepository) {
        this.databaseManager = databaseManager;
        this.clientRepository = clientRepository;
    }

    @Override
    public Optional<SavedPaymentMethod> findById(String savedMethodId) {
        String sql = "SELECT * FROM saved_payment_methods WHERE saved_method_id = ?";
        try (Connection c = databaseManager.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            s.setString(1, savedMethodId);
            try (ResultSet rs = s.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
                return Optional.empty();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to read saved payment method.", e);
        }
    }

    @Override
    public List<SavedPaymentMethod> findByClient(String clientId) {
        String sql = "SELECT * FROM saved_payment_methods WHERE client_id = ? ORDER BY display_label";
        try (Connection c = databaseManager.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            s.setString(1, clientId);
            try (ResultSet rs = s.executeQuery()) {
                List<SavedPaymentMethod> list = new ArrayList<>();
                while (rs.next()) list.add(map(rs));
                return list;
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to read saved payment methods.", e);
        }
    }

    @Override
    public void save(SavedPaymentMethod method) {
        String sql = "INSERT INTO saved_payment_methods(saved_method_id, client_id, method_type, display_label, payment_details, payment_detail_data) VALUES(?, ?, ?, ?, ?, ?) ON CONFLICT(saved_method_id) DO UPDATE SET client_id=excluded.client_id, method_type=excluded.method_type, display_label=excluded.display_label, payment_details=excluded.payment_details, payment_detail_data=excluded.payment_detail_data";
        try (Connection c = databaseManager.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            s.setString(1, method.getSavedMethodId());
            s.setString(2, method.getClient().getUserId());
            s.setString(3, method.getMethodType().name());
            s.setString(4, method.getDisplayLabel());
            s.setString(5, method.getPaymentDetails());
            s.setString(6, method.getPaymentDetailData());
            s.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Failed to save payment method.", e);
        }
    }

    @Override
    public void delete(String savedMethodId) {
        String sql = "DELETE FROM saved_payment_methods WHERE saved_method_id = ?";
        try (Connection c = databaseManager.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            s.setString(1, savedMethodId);
            s.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete payment method.", e);
        }
    }

    private SavedPaymentMethod map(ResultSet rs) throws Exception {
        Client client = clientRepository.findById(rs.getString("client_id")).orElseThrow();
        return new SavedPaymentMethod(rs.getString("saved_method_id"), client, PaymentMethodType.valueOf(rs.getString("method_type")), rs.getString("display_label"), rs.getString("payment_details"), rs.getString("payment_detail_data"));
    }
}
