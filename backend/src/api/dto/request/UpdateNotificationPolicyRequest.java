package backend.api.dto.request;

public class UpdateNotificationPolicyRequest {

    private String adminId;
    private boolean notifyOnBookingRequested;
    private boolean notifyOnBookingAccepted;
    private boolean notifyOnBookingRejected;
    private boolean notifyOnPaymentProcessed;
    private boolean notifyOnBookingCancelled;
    private boolean notifyOnConsultantApprovalDecision;

    public UpdateNotificationPolicyRequest() {
    }

    public String getAdminId() {
        return this.adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public boolean isNotifyOnBookingRequested() {
        return this.notifyOnBookingRequested;
    }

    public void setNotifyOnBookingRequested(boolean notifyOnBookingRequested) {
        this.notifyOnBookingRequested = notifyOnBookingRequested;
    }

    public boolean isNotifyOnBookingAccepted() {
        return this.notifyOnBookingAccepted;
    }

    public void setNotifyOnBookingAccepted(boolean notifyOnBookingAccepted) {
        this.notifyOnBookingAccepted = notifyOnBookingAccepted;
    }

    public boolean isNotifyOnBookingRejected() {
        return this.notifyOnBookingRejected;
    }

    public void setNotifyOnBookingRejected(boolean notifyOnBookingRejected) {
        this.notifyOnBookingRejected = notifyOnBookingRejected;
    }

    public boolean isNotifyOnPaymentProcessed() {
        return this.notifyOnPaymentProcessed;
    }

    public void setNotifyOnPaymentProcessed(boolean notifyOnPaymentProcessed) {
        this.notifyOnPaymentProcessed = notifyOnPaymentProcessed;
    }

    public boolean isNotifyOnBookingCancelled() {
        return this.notifyOnBookingCancelled;
    }

    public void setNotifyOnBookingCancelled(boolean notifyOnBookingCancelled) {
        this.notifyOnBookingCancelled = notifyOnBookingCancelled;
    }

    public boolean isNotifyOnConsultantApprovalDecision() {
        return this.notifyOnConsultantApprovalDecision;
    }

    public void setNotifyOnConsultantApprovalDecision(boolean notifyOnConsultantApprovalDecision) {
        this.notifyOnConsultantApprovalDecision = notifyOnConsultantApprovalDecision;
    }
}