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

    public String getPolicyId() { return policyId; }
    public double getRefundPercentBeforeDeadline() { return refundPercentBeforeDeadline; }
    public void setRefundPercentBeforeDeadline(double percent) { this.refundPercentBeforeDeadline = percent; }
    public double getRefundPercentAfterDeadline() { return refundPercentAfterDeadline; }
    public void setRefundPercentAfterDeadline(double percent) { this.refundPercentAfterDeadline = percent; }

    public double calculateRefund(Booking booking, LocalDateTime now, CancellationPolicy cancellationPolicy) {
        LocalDateTime deadline = booking.getSlot().getStartDateTime().minusHours(cancellationPolicy.getCancellationDeadlineHours());
        double percent = (now.isBefore(deadline) || now.isEqual(deadline)) ? this.refundPercentBeforeDeadline : this.refundPercentAfterDeadline;
        return booking.getAgreedPrice() * percent / 100.0;
    }
}
