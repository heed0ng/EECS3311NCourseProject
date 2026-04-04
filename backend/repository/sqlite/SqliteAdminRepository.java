package backend.repository.sqlite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import backend.model.user.Admin;
import backend.repository.AdminRepository;

public class SqliteAdminRepository implements AdminRepository {
    private final DatabaseManager databaseManager;

    public SqliteAdminRepository(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Override
    public Optional<Admin> findById(String adminId) {
        String sql = "SELECT user_id, name, email FROM admins WHERE user_id = ?";
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, adminId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(
                        new Admin(
                            resultSet.getString("user_id"),
                            resultSet.getString("name"),
                            resultSet.getString("email")
                        )
                    );
                }
                return Optional.empty();
            }
        } catch (Exception exception) {
            throw new RuntimeException("Failed to read admin.", exception);
        }
    }

    @Override
    public List<Admin> findAll() {
        String sql = "SELECT user_id, name, email FROM admins ORDER BY user_id";
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            List<Admin> admins = new ArrayList<>();
            while (resultSet.next()) {
                admins.add(
                    new Admin(
                        resultSet.getString("user_id"),
                        resultSet.getString("name"),
                        resultSet.getString("email")
                    )
                );
            }
            return admins;
        } catch (Exception exception) {
            throw new RuntimeException("Failed to read admins.", exception);
        }
    }

    @Override
    public void save(Admin admin) {
        String sql =
            "INSERT INTO admins(user_id, name, email) VALUES(?, ?, ?) " +
            "ON CONFLICT(user_id) DO UPDATE SET " +
            "name = excluded.name, email = excluded.email";

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, admin.getUserId());
            statement.setString(2, admin.getName());
            statement.setString(3, admin.getEmail());
            statement.executeUpdate();
        } catch (Exception exception) {
            throw new RuntimeException("Failed to save admin.", exception);
        }
    }
}