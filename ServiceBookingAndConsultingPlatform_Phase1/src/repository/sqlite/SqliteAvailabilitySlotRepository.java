package repository.sqlite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import model.core.AvailabilitySlot;
import model.user.Consultant;
import repository.AvailabilitySlotRepository;
import util.ConsultantApprovalStatus;

public class SqliteAvailabilitySlotRepository implements AvailabilitySlotRepository {
    private final DatabaseManager databaseManager;

    public SqliteAvailabilitySlotRepository(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Override
    public Optional<AvailabilitySlot> findById(String slotId) {
        String sql = baseSql() + " WHERE a.slot_id = ?";
        try (Connection c = databaseManager.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            s.setString(1, slotId);
            try (ResultSet rs = s.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
                return Optional.empty();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to read slot.", e);
        }
    }

    @Override
    public List<AvailabilitySlot> findAvailableByConsultant(String consultantId) {
        return this.findMany(baseSql() + " WHERE a.consultant_id = ? AND a.available = 1 AND c.approval_status = 'APPROVED' ORDER BY a.start_datetime", consultantId);
    }
    @Override
    public List<AvailabilitySlot> findByConsultant(String consultantId) {
        return this.findMany(baseSql() + " WHERE a.consultant_id = ? ORDER BY a.start_datetime", consultantId);
    }
    @Override
    public List<AvailabilitySlot> findAllAvailable() {
        return this.findMany(baseSql() + " WHERE a.available = 1 AND c.approval_status = 'APPROVED' ORDER BY a.start_datetime", null);
    }

    @Override
    public void save(AvailabilitySlot slot) {
        String sql = "INSERT INTO availability_slots(slot_id, consultant_id, start_datetime, end_datetime, available) VALUES(?, ?, ?, ?, ?) ON CONFLICT(slot_id) DO UPDATE SET consultant_id=excluded.consultant_id, start_datetime=excluded.start_datetime, end_datetime=excluded.end_datetime, available=excluded.available";
        try (Connection c = databaseManager.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            s.setString(1, slot.getSlotId());
            s.setString(2, slot.getConsultant().getUserId());
            s.setString(3, slot.getStartDateTime().toString());
            s.setString(4, slot.getEndDateTime().toString());
            s.setInt(5, slot.isAvailable() ? 1 : 0);
            s.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Failed to save slot.", e);
        }
    }

    private String baseSql() {
        return "SELECT a.slot_id, a.start_datetime, a.end_datetime, a.available, c.user_id AS consultant_id, c.name AS consultant_name, c.email AS consultant_email, c.approval_status FROM availability_slots a JOIN consultants c ON a.consultant_id = c.user_id";
    }

    private List<AvailabilitySlot> findMany(String sql, String consultantId) {
        try (Connection c = databaseManager.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            if (consultantId != null) s.setString(1, consultantId);
            try (ResultSet rs = s.executeQuery()) {
                List<AvailabilitySlot> list = new ArrayList<>();
                while (rs.next()) list.add(map(rs));
                return list;
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to read slots.", e);
        }
    }

    private AvailabilitySlot map(ResultSet rs) throws Exception {
        Consultant consultant = new Consultant(rs.getString("consultant_id"), rs.getString("consultant_name"), rs.getString("consultant_email"), ConsultantApprovalStatus.valueOf(rs.getString("approval_status")));
        return new AvailabilitySlot(rs.getString("slot_id"), consultant, LocalDateTime.parse(rs.getString("start_datetime")), LocalDateTime.parse(rs.getString("end_datetime")), rs.getInt("available") == 1);
    }
}
