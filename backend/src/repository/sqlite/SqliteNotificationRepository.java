package backend.repository.sqlite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import backend.model.notification.UserNotification;
import backend.repository.NotificationRepository;

public class SqliteNotificationRepository implements NotificationRepository {

    private final DatabaseManager databaseManager;

    public SqliteNotificationRepository(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Override
    public void save(UserNotification notification) {
        String sql =
            "INSERT INTO notifications (" +
            "source_event_id, recipient_user_id, recipient_role, event_type, message, occurred_at, is_read" +
            ") VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (
            Connection connection = this.databaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, notification.getSourceEventId());
            statement.setString(2, notification.getRecipientUserId());
            statement.setString(3, notification.getRecipientRole());
            statement.setString(4, notification.getEventType());
            statement.setString(5, notification.getMessage());
            statement.setString(6, notification.getOccurredAt().toString());
            statement.setInt(7, notification.isRead() ? 1 : 0);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to save notification.", exception);
        }
    }

    @Override
    public List<UserNotification> findByRecipient(String recipientRole, String recipientUserId) {
        String sql =
            "SELECT notification_id, source_event_id, recipient_user_id, recipient_role, " +
            "event_type, message, occurred_at, is_read " +
            "FROM notifications " +
            "WHERE recipient_role = ? AND recipient_user_id = ? " +
            "ORDER BY occurred_at DESC, notification_id DESC";

        List<UserNotification> notifications = new ArrayList<>();

        try (
            Connection connection = this.databaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, recipientRole);
            statement.setString(2, recipientUserId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    notifications.add(
                        new UserNotification(resultSet.getLong("notification_id"), resultSet.getString("source_event_id"),
                            resultSet.getString("recipient_user_id"), resultSet.getString("recipient_role"),
                            resultSet.getString("event_type"), resultSet.getString("message"), 
                            LocalDateTime.parse(resultSet.getString("occurred_at")), resultSet.getInt("is_read") == 1));
                }
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to load notifications.", exception);
        }

        return notifications;
    }
}