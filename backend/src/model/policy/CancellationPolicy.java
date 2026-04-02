package model.policy;

import java.time.LocalDateTime;

import model.core.Booking;

public class CancellationPolicy {
    private final String policyId;
    private int cancellationDeadlineHours;

    public CancellationPolicy(String policyId, int cancellationDeadlineHours) {
        this.policyId = policyId;
        this.cancellationDeadlineHours = cancellationDeadlineHours;
    }

    public String getPolicyId() { return policyId; }
    public int getCancellationDeadlineHours() { return cancellationDeadlineHours; }
    public void setCancellationDeadlineHours(int cancellationDeadlineHours) { this.cancellationDeadlineHours = cancellationDeadlineHours; }

    public boolean canCancel(Booking booking, LocalDateTime now) {
        LocalDateTime deadline = booking.getSlot().getStartDateTime().minusHours(this.cancellationDeadlineHours);
        return now.isBefore(booking.getSlot().getStartDateTime()) && (now.isBefore(deadline) || now.isEqual(deadline));
    }
}
