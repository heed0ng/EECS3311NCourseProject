package backend.api.dto.request;

public class UpdateRefundPolicyRequest {

    private String adminId;
    private double refundPercentBeforeDeadline;
    private double refundPercentAfterDeadline;

    public UpdateRefundPolicyRequest() {
    }

    public String getAdminId() {
        return this.adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public double getRefundPercentBeforeDeadline() {
        return this.refundPercentBeforeDeadline;
    }

    public void setRefundPercentBeforeDeadline(double refundPercentBeforeDeadline) {
        this.refundPercentBeforeDeadline = refundPercentBeforeDeadline;
    }

    public double getRefundPercentAfterDeadline() {
        return this.refundPercentAfterDeadline;
    }

    public void setRefundPercentAfterDeadline(double refundPercentAfterDeadline) {
        this.refundPercentAfterDeadline = refundPercentAfterDeadline;
    }
}