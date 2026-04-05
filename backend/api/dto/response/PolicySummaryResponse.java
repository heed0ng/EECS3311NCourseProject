package backend.api.dto.response;

public class PolicySummaryResponse {
    private String cancellationPolicySummary;
    private String pricingPolicySummary;
    private String notificationPolicySummary;
    private String refundPolicySummary;

    public PolicySummaryResponse() {
    }

    public PolicySummaryResponse(
            String cancellationPolicySummary,
            String pricingPolicySummary,
            String notificationPolicySummary,
            String refundPolicySummary) {
        this.cancellationPolicySummary = cancellationPolicySummary;
        this.pricingPolicySummary = pricingPolicySummary;
        this.notificationPolicySummary = notificationPolicySummary;
        this.refundPolicySummary = refundPolicySummary;
    }

    public String getCancellationPolicySummary() {
        return this.cancellationPolicySummary;
    }

    public void setCancellationPolicySummary(String cancellationPolicySummary) {
        this.cancellationPolicySummary = cancellationPolicySummary;
    }

    public String getPricingPolicySummary() {
        return this.pricingPolicySummary;
    }

    public void setPricingPolicySummary(String pricingPolicySummary) {
        this.pricingPolicySummary = pricingPolicySummary;
    }

    public String getNotificationPolicySummary() {
        return this.notificationPolicySummary;
    }

    public void setNotificationPolicySummary(String notificationPolicySummary) {
        this.notificationPolicySummary = notificationPolicySummary;
    }

    public String getRefundPolicySummary() {
        return this.refundPolicySummary;
    }

    public void setRefundPolicySummary(String refundPolicySummary) {
        this.refundPolicySummary = refundPolicySummary;
    }
}