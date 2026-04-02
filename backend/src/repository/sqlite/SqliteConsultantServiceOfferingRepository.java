package repository.sqlite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import model.core.ConsultantServiceOffering;
import model.core.ConsultingService;
import model.user.Consultant;
import repository.ConsultantServiceOfferingRepository;
import util.ConsultantApprovalStatus;

public class SqliteConsultantServiceOfferingRepository implements ConsultantServiceOfferingRepository {
    private final DatabaseManager databaseManager;

    public SqliteConsultantServiceOfferingRepository(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Override
    public Optional<ConsultantServiceOffering> findById(String offeringId) {
        String sql = baseSql() + " WHERE o.offering_id = ?";
        try (Connection c = databaseManager.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            s.setString(1, offeringId);
            try (ResultSet rs = s.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
                return Optional.empty();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to read offering.", e);
        }
    }

    @Override
    public List<ConsultantServiceOffering> findAllActive() {
        String sql = baseSql() + " WHERE o.active = 1 AND c.approval_status = 'APPROVED' ORDER BY s.name";
        return this.findMany(sql, null);
    }

    @Override
    public List<ConsultantServiceOffering> findByConsultant(String consultantId) {
        String sql = baseSql() + " WHERE o.consultant_id = ? ORDER BY s.name";
        return this.findMany(sql, consultantId);
    }

    @Override
    public void save(ConsultantServiceOffering offering) {
        String sql = "INSERT INTO consultant_service_offerings(offering_id, consultant_id, service_id, custom_price, active) VALUES(?, ?, ?, ?, ?) ON CONFLICT(offering_id) DO UPDATE SET consultant_id=excluded.consultant_id, service_id=excluded.service_id, custom_price=excluded.custom_price, active=excluded.active";
        try (Connection c = databaseManager.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            s.setString(1, offering.getOfferingId());
            s.setString(2, offering.getConsultant().getUserId());
            s.setString(3, offering.getConsultingService().getServiceId());
            if (offering.getCustomPrice() == null) s.setNull(4, java.sql.Types.REAL); else s.setDouble(4, offering.getCustomPrice());
            s.setInt(5, offering.isActive() ? 1 : 0);
            s.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Failed to save offering.", e);
        }
    }

    private String baseSql() {
        return "SELECT o.offering_id, o.custom_price, o.active AS offering_active, "
                + "c.user_id AS consultant_id, c.name AS consultant_name, c.email AS consultant_email, c.approval_status, "
                + "s.service_id, s.name AS service_name, s.description AS service_description, s.duration_minutes, s.base_price, s.active AS service_active "
                + "FROM consultant_service_offerings o JOIN consultants c ON o.consultant_id = c.user_id "
                + "JOIN consulting_services s ON o.service_id = s.service_id";
    }

    private List<ConsultantServiceOffering> findMany(String sql, String id) {
        try (Connection c = databaseManager.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            if (id != null) s.setString(1, id);
            try (ResultSet rs = s.executeQuery()) {
                List<ConsultantServiceOffering> list = new ArrayList<>();
                while (rs.next()) list.add(map(rs));
                return list;
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to read offerings.", e);
        }
    }

    private ConsultantServiceOffering map(ResultSet rs) throws Exception {
        Consultant consultant = new Consultant(rs.getString("consultant_id"), rs.getString("consultant_name"), rs.getString("consultant_email"), ConsultantApprovalStatus.valueOf(rs.getString("approval_status")));
        ConsultingService service = new ConsultingService(rs.getString("service_id"), rs.getString("service_name"), rs.getString("service_description"), rs.getInt("duration_minutes"), rs.getDouble("base_price"), rs.getInt("service_active") == 1);
        Double customPrice = rs.getObject("custom_price") == null ? null : rs.getDouble("custom_price");
        return new ConsultantServiceOffering(rs.getString("offering_id"), consultant, service, customPrice, rs.getInt("offering_active") == 1);
    }
}
