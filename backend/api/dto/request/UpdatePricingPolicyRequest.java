package backend.api.dto.request;

public class UpdatePricingPolicyRequest {

    private String adminId;
    private boolean allowConsultantCustomPrice;

    public UpdatePricingPolicyRequest() {
    }

    public String getAdminId() {
        return this.adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public boolean isAllowConsultantCustomPrice() {
        return this.allowConsultantCustomPrice;
    }

    public void setAllowConsultantCustomPrice(boolean allowConsultantCustomPrice) {
        this.allowConsultantCustomPrice = allowConsultantCustomPrice;
    }
}