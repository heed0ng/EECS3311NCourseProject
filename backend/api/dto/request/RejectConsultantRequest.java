package backend.api.dto.request;

public class RejectConsultantRequest {

    private String adminId;

    public RejectConsultantRequest() {
    }

    public String getAdminId() {
        return this.adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }
}