package backend.api.dto.request;

public class ApproveConsultantRequest {

    private String adminId;

    public ApproveConsultantRequest() {
    }

    public String getAdminId() {
        return this.adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }
}