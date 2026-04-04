package backend.repository.sqlite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import backend.model.user.Consultant;
import backend.repository.ConsultantRepository;
import backend.util.ConsultantApprovalStatus;

public class SqliteConsultantRepository implements ConsultantRepository {
    private final DatabaseManager databaseManager;

    public SqliteConsultantRepository(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Override
    public Optional<Consultant> findById(String consultantId) {
        String sql = "SELECT user_id, name, email, approval_status FROM consultants WHERE user_id = ?";
        try (Connection connection = databaseManager.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, consultantId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) return Optional.of(map(resultSet));
                return Optional.empty();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to read consultant.", e);
        }
    }

    @Override
    public List<Consultant> findAllApproved() {
        return this.findByStatus(ConsultantApprovalStatus.APPROVED.name());
    }

    @Override
    public List<Consultant> findPendingApproval() {
        return this.findByStatus(ConsultantApprovalStatus.PENDING.name());
    }

    @Override
    public List<Consultant> findAll() {
        String sql = "SELECT user_id, name, email, approval_status FROM consultants ORDER BY name";
        try (Connection connection = databaseManager.getConnection(); PreparedStatement statement = connection.prepareStatement(sql); ResultSet resultSet = statement.executeQuery()) {
            List<Consultant> consultants = new ArrayList<>();
            while (resultSet.next()) consultants.add(map(resultSet));
            return consultants;
        } catch (Exception e) {
            throw new RuntimeException("Failed to read consultants.", e);
        }
    }

    @Override
    public void save(Consultant consultant) {
        String sql = "INSERT INTO consultants(user_id, name, email, approval_status) VALUES(?, ?, ?, ?) ON CONFLICT(user_id) DO UPDATE SET name=excluded.name, email=excluded.email, approval_status=excluded.approval_status";
        try (Connection connection = databaseManager.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, consultant.getUserId());
            statement.setString(2, consultant.getName());
            statement.setString(3, consultant.getEmail());
            statement.setString(4, consultant.getApprovalStatus().name());
            statement.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Failed to save consultant.", e);
        }
    }

    private List<Consultant> findByStatus(String status) {
        String sql = "SELECT user_id, name, email, approval_status FROM consultants WHERE approval_status = ? ORDER BY name";
        try (Connection connection = databaseManager.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<Consultant> consultants = new ArrayList<>();
                while (resultSet.next()) consultants.add(map(resultSet));
                return consultants;
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to read consultants by status.", e);
        }
    }

    private Consultant map(ResultSet resultSet) throws Exception {
        return new Consultant(resultSet.getString("user_id"), resultSet.getString("name"), resultSet.getString("email"), ConsultantApprovalStatus.valueOf(resultSet.getString("approval_status")));
    }
}
