package backend.model.policy;

public class CustomPricingPolicy {
    private final String policyId;
    private boolean allowConsultantCustomPrice;

    public CustomPricingPolicy(String policyId, boolean allowConsultantCustomPrice) {
        this.policyId = policyId;
        this.allowConsultantCustomPrice = allowConsultantCustomPrice;
    }

    public String getPolicyId() { return policyId; }
    public boolean isAllowConsultantCustomPrice() { return allowConsultantCustomPrice; }
    public void setAllowConsultantCustomPrice(boolean allowConsultantCustomPrice) {
        this.allowConsultantCustomPrice = allowConsultantCustomPrice;
    }
}
