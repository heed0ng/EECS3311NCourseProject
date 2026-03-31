package model.policy;

public class PricingPolicy {
    private final String policyId;
    private boolean allowConsultantCustomPrice;

    public PricingPolicy(String policyId, boolean allowConsultantCustomPrice) {
        this.policyId = policyId;
        this.allowConsultantCustomPrice = allowConsultantCustomPrice;
    }

    public String getPolicyId() { return policyId; }
    public boolean isAllowConsultantCustomPrice() { return allowConsultantCustomPrice; }
    public void setAllowConsultantCustomPrice(boolean allowConsultantCustomPrice) {
        this.allowConsultantCustomPrice = allowConsultantCustomPrice;
    }
}
