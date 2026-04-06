package backend.model.policy;

import java.time.LocalDateTime;

import backend.model.core.Booking;

public class RefundPolicy {
    private final String policyId;
    private double refundPercentBeforeDeadline;
    private double refundPercentAfterDeadline;

    public RefundPolicy(String policyId, double refundPercentBeforeDeadline, double refundPercentAfterDeadline) {
        this.policyId = policyId;
        this.refundPercentBeforeDeadline = refundPercentBeforeDeadline;
        this.refundPercentAfterDeadline = refundPercentAfterDeadline;
    }

    public String getPolicyId() {
        return this.policyId;
    }

    public double getRefundPercentBeforeDeadline() {
        return this.refundPercentBeforeDeadline;
    }

    public void setRefundPercentBeforeDeadline(double percent) {
        this.refundPercentBeforeDeadline = percent;
    }

    public double getRefundPercentAfterDeadline() {
        return this.refundPercentAfterDeadline;
    }

    public void setRefundPercentAfterDeadline(double percent) {
        this.refundPercentAfterDeadline = percent;
    }

    public double calculateRefund(Booking booking, LocalDateTime now, CancellationPolicy cancellationPolicy) {
        if (!cancellationPolicy.canCancel(booking, now)) return 0.0;
        return booking.getPrice() * this.refundPercentBeforeDeadline / 100.0;
    }
}