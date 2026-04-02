package repository.sqlite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import model.core.ConsultingService;
import repository.ConsultingServiceRepository;

public class SqliteConsultingServiceRepository implements ConsultingServiceRepository {
    private final DatabaseManager databaseManager;

    public SqliteConsultingServiceRepository(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Override
    public Optional<ConsultingService> findById(String serviceId) {
        String sql = "SELECT * FROM consulting_services WHERE service_id = ?";
        try (Connection connection = databaseManager.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, serviceId);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
                return Optional.empty();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to read consulting service.", e);
        }
    }

    @Override
    public List<ConsultingService> findAllActive() { return this.findByActive(true); }
    @Override
    public List<ConsultingService> findAll() { return this.findByActive(false, true); }

    @Override
    public void save(ConsultingService service) {
        String sql = "INSERT INTO consulting_services(service_id, name, description, duration_minutes, base_price, active) VALUES(?, ?, ?, ?, ?, ?) ON CONFLICT(service_id) DO UPDATE SET name=excluded.name, description=excluded.description, duration_minutes=excluded.duration_minutes, base_price=excluded.base_price, active=excluded.active";
        try (Connection connection = databaseManager.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, service.getServiceId());
            statement.setString(2, service.getName());
            statement.setString(3, service.getDescription());
            statement.setInt(4, service.getDurationMinutes());
            statement.setDouble(5, service.getBasePrice());
            statement.setInt(6, service.isActive() ? 1 : 0);
            statement.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Failed to save consulting service.", e);
        }
    }

    private List<ConsultingService> findByActive(boolean active) { return findByActive(active, false); }
    
    private List<ConsultingService> findByActive(boolean active, boolean ignoreFilter) {
        String sql = ignoreFilter ? "SELECT * FROM consulting_services ORDER BY name" : "SELECT * FROM consulting_services WHERE active = ? ORDER BY name";
        try (Connection connection = databaseManager.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            if (!ignoreFilter) statement.setInt(1, active ? 1 : 0);
            try (ResultSet rs = statement.executeQuery()) {
                List<ConsultingService> list = new ArrayList<>();
                while (rs.next()) list.add(map(rs));
                return list;
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to read consulting services.", e);
        }
    }

    private ConsultingService map(ResultSet rs) throws Exception {
        return new ConsultingService(rs.getString("service_id"), rs.getString("name"), rs.getString("description"), rs.getInt("duration_minutes"), rs.getDouble("base_price"), rs.getInt("active") == 1);
    }
}
