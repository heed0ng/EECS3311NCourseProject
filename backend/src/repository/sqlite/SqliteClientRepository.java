package backend.repository.sqlite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import backend.model.user.Client;
import backend.repository.ClientRepository;

public class SqliteClientRepository implements ClientRepository {
    private final DatabaseManager databaseManager;

    public SqliteClientRepository(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Override
    public Optional<Client> findById(String clientId) {
        String sql = "SELECT user_id, name, email FROM clients WHERE user_id = ?";
        try (Connection connection = databaseManager.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, clientId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(new Client(resultSet.getString("user_id"), resultSet.getString("name"), resultSet.getString("email")));
                }
                return Optional.empty();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to read client.", e);
        }
    }

    @Override
    public List<Client> findAll() {
        List<Client> clients = new ArrayList<>();
        String sql = "SELECT user_id, name, email FROM clients ORDER BY name";
        try (Connection connection = databaseManager.getConnection(); PreparedStatement statement = connection.prepareStatement(sql); ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                clients.add(new Client(resultSet.getString("user_id"), resultSet.getString("name"), resultSet.getString("email")));
            }
            return clients;
        } catch (Exception e) {
            throw new RuntimeException("Failed to read clients.", e);
        }
    }

    @Override
    public void save(Client client) {
        String sql = "INSERT INTO clients(user_id, name, email) VALUES(?, ?, ?) ON CONFLICT(user_id) DO UPDATE SET name=excluded.name, email=excluded.email";
        try (Connection connection = databaseManager.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, client.getUserId());
            statement.setString(2, client.getName());
            statement.setString(3, client.getEmail());
            statement.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Failed to save client.", e);
        }
    }
}
