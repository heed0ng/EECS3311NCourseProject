package repository.sqlite;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class SchemaInitializer {
    private final DatabaseManager databaseManager;

    public SchemaInitializer(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public void initialize() {
        try (Connection connection = databaseManager.getConnection(); Statement statement = connection.createStatement()) {
            statement.executeUpdate("PRAGMA foreign_keys = ON");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS clients (user_id TEXT PRIMARY KEY, name TEXT NOT NULL, email TEXT NOT NULL)");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS admins (user_id TEXT PRIMARY KEY, name TEXT NOT NULL, email TEXT NOT NULL)");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS consultants (user_id TEXT PRIMARY KEY, name TEXT NOT NULL, email TEXT NOT NULL, approval_status TEXT NOT NULL)");
           
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS consulting_services (service_id TEXT PRIMARY KEY, name TEXT NOT NULL, description TEXT, duration_minutes INTEGER NOT NULL, base_price REAL NOT NULL, active INTEGER NOT NULL)");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS consultant_service_offerings (offering_id TEXT PRIMARY KEY, consultant_id TEXT NOT NULL, service_id TEXT NOT NULL, custom_price REAL, active INTEGER NOT NULL, FOREIGN KEY (consultant_id) REFERENCES consultants(user_id), FOREIGN KEY (service_id) REFERENCES consulting_services(service_id))");
            
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS availability_slots (slot_id TEXT PRIMARY KEY, consultant_id TEXT NOT NULL, start_datetime TEXT NOT NULL, end_datetime TEXT NOT NULL, available INTEGER NOT NULL, FOREIGN KEY (consultant_id) REFERENCES consultants(user_id))");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS bookings (booking_id TEXT PRIMARY KEY, client_id TEXT NOT NULL, offering_id TEXT NOT NULL, slot_id TEXT NOT NULL UNIQUE, state_name TEXT NOT NULL, created_at TEXT NOT NULL, last_updated_at TEXT NOT NULL, agreed_price REAL NOT NULL, FOREIGN KEY (client_id) REFERENCES clients(user_id), FOREIGN KEY (offering_id) REFERENCES consultant_service_offerings(offering_id), FOREIGN KEY (slot_id) REFERENCES availability_slots(slot_id))");
            
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS saved_payment_methods (saved_method_id TEXT PRIMARY KEY, client_id TEXT NOT NULL, method_type TEXT NOT NULL, display_label TEXT NOT NULL, payment_details TEXT NOT NULL, payment_detail_data TEXT, FOREIGN KEY (client_id) REFERENCES clients(user_id))");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS payment_transactions (transaction_id TEXT PRIMARY KEY, booking_id TEXT NOT NULL, client_id TEXT NOT NULL, transaction_type TEXT NOT NULL, status TEXT NOT NULL, method_type TEXT NOT NULL, amount REAL NOT NULL, created_at TEXT NOT NULL, FOREIGN KEY (booking_id) REFERENCES bookings(booking_id), FOREIGN KEY (client_id) REFERENCES clients(user_id))");
            
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS cancellation_policies (policy_id TEXT PRIMARY KEY, cancellation_deadline_hours INTEGER NOT NULL)");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS refund_policies (policy_id TEXT PRIMARY KEY, refund_percent_before_deadline REAL NOT NULL, refund_percent_after_deadline REAL NOT NULL)");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS pricing_policies (policy_id TEXT PRIMARY KEY, allow_consultant_custom_price INTEGER NOT NULL)");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS notification_policies (policy_id TEXT PRIMARY KEY, notify_on_booking_requested INTEGER NOT NULL, notify_on_booking_accepted INTEGER NOT NULL, notify_on_booking_rejected INTEGER NOT NULL, notify_on_payment_processed INTEGER NOT NULL, notify_on_booking_cancelled INTEGER NOT NULL, notify_on_consultant_approval_decision INTEGER NOT NULL)");
            
            statement.executeUpdate("INSERT OR IGNORE INTO cancellation_policies(policy_id, cancellation_deadline_hours) VALUES ('default-cancellation', 24)");
            statement.executeUpdate("INSERT OR IGNORE INTO refund_policies(policy_id, refund_percent_before_deadline, refund_percent_after_deadline) VALUES ('default-refund', 100.0, 0.0)");
            statement.executeUpdate("INSERT OR IGNORE INTO pricing_policies(policy_id, allow_consultant_custom_price) VALUES ('default-pricing', 1)");
            statement.executeUpdate("INSERT OR IGNORE INTO notification_policies(policy_id, notify_on_booking_requested, notify_on_booking_accepted, notify_on_booking_rejected, notify_on_payment_processed, notify_on_booking_cancelled, notify_on_consultant_approval_decision) VALUES ('default-notification', 1, 1, 1, 1, 1, 1)");
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to initialize database schema.", exception);
        }
    }
}
