package backend.repository.sqlite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import backend.model.core.*;
import backend.model.user.Client;
import backend.model.user.Consultant;
import backend.repository.BookingRepository;
import backend.state.*;
import backend.util.ConsultantApprovalStatus;

public class SqliteBookingRepository implements BookingRepository {
    private final DatabaseManager databaseManager;

    public SqliteBookingRepository(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Override
    public Optional<Booking> findById(String bookingId) {
        String sql = baseSql() + " WHERE b.booking_id = ?";
        try (Connection c = databaseManager.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            s.setString(1, bookingId);
            try (ResultSet rs = s.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
                return Optional.empty();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to read booking.", e);
        }
    }

    @Override
    public List<Booking> findByClient(String clientId) {
        return this.findMany(baseSql() + " WHERE cl.user_id = ? ORDER BY b.created_at DESC", clientId);
    }

    @Override
    public List<Booking> findByConsultant(String consultantId) {
        return this.findMany(baseSql() + " WHERE c.user_id = ? ORDER BY b.created_at DESC", consultantId);
    }

    @Override
    public List<Booking> findPendingRequestsForConsultant(String consultantId) {
        return this.findMany(baseSql() + " WHERE c.user_id = ? AND b.state_name = 'Requested' ORDER BY b.created_at ASC", consultantId);
    }

    @Override
    public List<Booking> findAll() {
        return this.findMany(baseSql() + " ORDER BY b.created_at DESC", null);
    }

    @Override
    public void save(Booking booking) {
        String sql = "INSERT INTO bookings(booking_id, client_id, offering_id, slot_id, state_name, created_at, last_updated_at, agreed_price) VALUES(?, ?, ?, ?, ?, ?, ?, ?) ON CONFLICT(booking_id) DO UPDATE SET client_id=excluded.client_id, offering_id=excluded.offering_id, slot_id=excluded.slot_id, state_name=excluded.state_name, created_at=excluded.created_at, last_updated_at=excluded.last_updated_at, agreed_price=excluded.agreed_price";
        try (Connection c = databaseManager.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            s.setString(1, booking.getBookingId());
            s.setString(2, booking.getClient().getUserId());
            s.setString(3, booking.getOffering().getOfferingId());
            s.setString(4, booking.getSlot().getSlotId());
            s.setString(5, booking.getStateName());
            s.setString(6, booking.getCreatedAt().toString());
            s.setString(7, booking.getLastUpdatedAt().toString());
            s.setDouble(8, booking.getAgreedPrice());
            s.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Failed to save booking.", e);
        }
    }

    private String baseSql() {
        return "SELECT b.booking_id, b.state_name, b.created_at, b.last_updated_at, b.agreed_price, "
                + "cl.user_id AS client_id, cl.name AS client_name, cl.email AS client_email, "
                + "o.offering_id, o.custom_price, o.active AS offering_active, "
                + "c.user_id AS consultant_id, c.name AS consultant_name, c.email AS consultant_email, c.approval_status, "
                + "s.service_id, s.name AS service_name, s.description AS service_description, s.duration_minutes, s.base_price, s.active AS service_active, "
                + "a.slot_id, a.start_datetime, a.end_datetime, a.available AS slot_available "
                + "FROM bookings b "
                + "JOIN clients cl ON b.client_id = cl.user_id "
                + "JOIN consultant_service_offerings o ON b.offering_id = o.offering_id "
                + "JOIN consultants c ON o.consultant_id = c.user_id "
                + "JOIN consulting_services s ON o.service_id = s.service_id "
                + "JOIN availability_slots a ON b.slot_id = a.slot_id";
    }

    private List<Booking> findMany(String sql, String id) {
        try (Connection c = databaseManager.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            if (id != null) s.setString(1, id);
            try (ResultSet rs = s.executeQuery()) {
                List<Booking> list = new ArrayList<>();
                while (rs.next()) list.add(map(rs));
                return list;
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to read bookings.", e);
        }
    }

    private Booking map(ResultSet rs) throws Exception {
        Client client = new Client(rs.getString("client_id"), rs.getString("client_name"), rs.getString("client_email"));
        Consultant consultant = new Consultant(rs.getString("consultant_id"), rs.getString("consultant_name"), rs.getString("consultant_email"), ConsultantApprovalStatus.valueOf(rs.getString("approval_status")));
        ConsultingService service = new ConsultingService(rs.getString("service_id"), rs.getString("service_name"), rs.getString("service_description"), rs.getInt("duration_minutes"), rs.getDouble("base_price"), rs.getInt("service_active") == 1);
        Double customPrice = rs.getObject("custom_price") == null ? null : rs.getDouble("custom_price");
        ConsultantServiceOffering offering = new ConsultantServiceOffering(rs.getString("offering_id"), consultant, service, customPrice, rs.getInt("offering_active") == 1);
        AvailabilitySlot slot = new AvailabilitySlot(rs.getString("slot_id"), consultant, LocalDateTime.parse(rs.getString("start_datetime")), LocalDateTime.parse(rs.getString("end_datetime")), rs.getInt("slot_available") == 1);
        return new Booking(rs.getString("booking_id"), client, offering, slot, toState(rs.getString("state_name")), LocalDateTime.parse(rs.getString("created_at")), LocalDateTime.parse(rs.getString("last_updated_at")), rs.getDouble("agreed_price"));
    }

    private BookingState toState(String name) {
        switch (name) {
            case "Requested": return new RequestedState();
            case "Confirmed": return new ConfirmedState();
            case "Pending Payment": return new PendingPaymentState();
            case "Paid": return new PaidState();
            case "Rejected": return new RejectedState();
            case "Cancelled": return new CancelledState();
            case "Completed": return new CompletedState();
            default: throw new IllegalArgumentException("Unknown booking state: " + name);
        }
    }
}
