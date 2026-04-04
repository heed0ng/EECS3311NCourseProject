package backend.model.policy;

public class NotificationPolicy {
    private final String policyId;
    private boolean notifyOnBookingRequested;
    private boolean notifyOnBookingAccepted;
    private boolean notifyOnBookingRejected;
    private boolean notifyOnPaymentProcessed;
    private boolean notifyOnBookingCancelled;
    private boolean notifyOnConsultantApprovalDecision;

    public NotificationPolicy(String policyId, boolean notifyOnBookingRequested, boolean notifyOnBookingAccepted, boolean notifyOnBookingRejected, 
    		boolean notifyOnPaymentProcessed, boolean notifyOnBookingCancelled, boolean notifyOnConsultantApprovalDecision) {
        this.policyId = policyId;
        this.notifyOnBookingRequested = notifyOnBookingRequested;
        this.notifyOnBookingAccepted = notifyOnBookingAccepted;
        this.notifyOnBookingRejected = notifyOnBookingRejected;
        this.notifyOnPaymentProcessed = notifyOnPaymentProcessed;
        this.notifyOnBookingCancelled = notifyOnBookingCancelled;
        this.notifyOnConsultantApprovalDecision = notifyOnConsultantApprovalDecision;
    }

    public String getPolicyId() { return policyId; }
    public boolean isNotifyOnBookingRequested() { return notifyOnBookingRequested; }
    public void setNotifyOnBookingRequested(boolean v) { this.notifyOnBookingRequested = v; }
    public boolean isNotifyOnBookingAccepted() { return notifyOnBookingAccepted; }
    public void setNotifyOnBookingAccepted(boolean v) { this.notifyOnBookingAccepted = v; }
    public boolean isNotifyOnBookingRejected() { return notifyOnBookingRejected; }
    public void setNotifyOnBookingRejected(boolean v) { this.notifyOnBookingRejected = v; }
    public boolean isNotifyOnPaymentProcessed() { return notifyOnPaymentProcessed; }
    public void setNotifyOnPaymentProcessed(boolean v) { this.notifyOnPaymentProcessed = v; }
    public boolean isNotifyOnBookingCancelled() { return notifyOnBookingCancelled; }
    public void setNotifyOnBookingCancelled(boolean v) { this.notifyOnBookingCancelled = v; }
    public boolean isNotifyOnConsultantApprovalDecision() { return notifyOnConsultantApprovalDecision; }
    public void setNotifyOnConsultantApprovalDecision(boolean v) { this.notifyOnConsultantApprovalDecision = v; }
}
