package repository.sqlite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import model.policy.CancellationPolicy;
import model.policy.NotificationPolicy;
import model.policy.CustomPricingPolicy;
import model.policy.RefundPolicy;
import repository.PolicyRepository;
// Currently only 1 policy exsits for each category.

public class SqlitePolicyRepository implements PolicyRepository {
    private final DatabaseManager databaseManager;

    public SqlitePolicyRepository(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Override
    public CancellationPolicy getCancellationPolicy() {
        String sql = "SELECT * FROM cancellation_policies LIMIT 1";
        try (Connection c = databaseManager.getConnection(); PreparedStatement s = c.prepareStatement(sql); ResultSet rs = s.executeQuery()) {
            if (rs.next()) return new CancellationPolicy(rs.getString("policy_id"), rs.getInt("cancellation_deadline_hours"));
            throw new RuntimeException("Cancellation policy missing.");
        } catch (Exception e) {
            throw new RuntimeException("Failed to read cancellation policy.", e);
        }
    }

    @Override
    public RefundPolicy getRefundPolicy() {
        String sql = "SELECT * FROM refund_policies LIMIT 1";
        try (Connection c = databaseManager.getConnection(); PreparedStatement s = c.prepareStatement(sql); ResultSet rs = s.executeQuery()) {
            if (rs.next()) return new RefundPolicy(rs.getString("policy_id"), rs.getDouble("refund_percent_before_deadline"), rs.getDouble("refund_percent_after_deadline"));
            throw new RuntimeException("Refund policy missing.");
        } catch (Exception e) {
            throw new RuntimeException("Failed to read refund policy.", e);
        }
    }

    @Override
    public CustomPricingPolicy getPricingPolicy() {
        String sql = "SELECT * FROM pricing_policies LIMIT 1";
        try (Connection c = databaseManager.getConnection(); PreparedStatement s = c.prepareStatement(sql); ResultSet rs = s.executeQuery()) {
            if (rs.next()) return new CustomPricingPolicy(rs.getString("policy_id"), rs.getInt("allow_consultant_custom_price") == 1);
            throw new RuntimeException("Pricing policy missing.");
        } catch (Exception e) {
            throw new RuntimeException("Failed to read pricing policy.", e);
        }
    }

    @Override
    public NotificationPolicy getNotificationPolicy() {
        String sql = "SELECT * FROM notification_policies LIMIT 1";
        try (Connection c = databaseManager.getConnection(); PreparedStatement s = c.prepareStatement(sql); ResultSet rs = s.executeQuery()) {
            if (rs.next()) {
                return new NotificationPolicy(rs.getString("policy_id"), rs.getInt("notify_on_booking_requested") == 1, rs.getInt("notify_on_booking_accepted") == 1, rs.getInt("notify_on_booking_rejected") == 1, rs.getInt("notify_on_payment_processed") == 1, rs.getInt("notify_on_booking_cancelled") == 1, rs.getInt("notify_on_consultant_approval_decision") == 1);
            }
            throw new RuntimeException("Notification policy missing.");
        } catch (Exception e) {
            throw new RuntimeException("Failed to read notification policy.", e);
        }
    }

    @Override
    public void saveCancellationPolicy(CancellationPolicy policy) {
        String sql = "UPDATE cancellation_policies SET cancellation_deadline_hours = ? WHERE policy_id = ?";
        try (Connection c = databaseManager.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            s.setInt(1, policy.getCancellationDeadlineHours());
            s.setString(2, policy.getPolicyId());
            s.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Failed to save cancellation policy.", e);
        }
    }

    @Override
    public void saveRefundPolicy(RefundPolicy policy) {
        String sql = "UPDATE refund_policies SET refund_percent_before_deadline = ?, refund_percent_after_deadline = ? WHERE policy_id = ?";
        try (Connection c = databaseManager.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            s.setDouble(1, policy.getRefundPercentBeforeDeadline());
            s.setDouble(2, policy.getRefundPercentAfterDeadline());
            s.setString(3, policy.getPolicyId());
            s.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Failed to save refund policy.", e);
        }
    }

    @Override
    public void savePricingPolicy(CustomPricingPolicy policy) {
        String sql = "UPDATE pricing_policies SET allow_consultant_custom_price = ? WHERE policy_id = ?";
        try (Connection c = databaseManager.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            s.setInt(1, policy.isAllowConsultantCustomPrice() ? 1 : 0);
            s.setString(2, policy.getPolicyId());
            s.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Failed to save pricing policy.", e);
        }
    }

    @Override
    public void saveNotificationPolicy(NotificationPolicy policy) {
        String sql = "UPDATE notification_policies SET notify_on_booking_requested = ?, notify_on_booking_accepted = ?, notify_on_booking_rejected = ?, notify_on_payment_processed = ?, notify_on_booking_cancelled = ?, notify_on_consultant_approval_decision = ? WHERE policy_id = ?";
        try (Connection c = databaseManager.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            s.setInt(1, policy.isNotifyOnBookingRequested() ? 1 : 0);
            s.setInt(2, policy.isNotifyOnBookingAccepted() ? 1 : 0);
            s.setInt(3, policy.isNotifyOnBookingRejected() ? 1 : 0);
            s.setInt(4, policy.isNotifyOnPaymentProcessed() ? 1 : 0);
            s.setInt(5, policy.isNotifyOnBookingCancelled() ? 1 : 0);
            s.setInt(6, policy.isNotifyOnConsultantApprovalDecision() ? 1 : 0);
            s.setString(7, policy.getPolicyId());
            s.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Failed to save notification policy.", e);
        }
    }
}
