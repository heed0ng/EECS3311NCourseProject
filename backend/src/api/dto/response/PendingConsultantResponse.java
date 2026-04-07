package backend.api.dto.response;

public class PendingConsultantResponse {

    private String consultantId;
    private String consultantName;
    private String email;
    private String approvalStatus;

    public PendingConsultantResponse() {
    }

    public PendingConsultantResponse(
            String consultantId,
            String consultantName,
            String email,
            String approvalStatus) {
        this.consultantId = consultantId;
        this.consultantName = consultantName;
        this.email = email;
        this.approvalStatus = approvalStatus;
    }

    public String getConsultantId() {
        return this.consultantId;
    }

    public void setConsultantId(String consultantId) {
        this.consultantId = consultantId;
    }

    public String getConsultantName() {
        return this.consultantName;
    }

    public void setConsultantName(String consultantName) {
        this.consultantName = consultantName;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getApprovalStatus() {
        return this.approvalStatus;
    }

    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }
}