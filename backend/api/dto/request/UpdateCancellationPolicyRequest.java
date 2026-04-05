package backend.api.dto.request;

public class UpdateCancellationPolicyRequest {

    private String adminId;
    private int cancellationDeadlineHours;

    public UpdateCancellationPolicyRequest() {
    }

    public String getAdminId() {
        return this.adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public int getCancellationDeadlineHours() {
        return this.cancellationDeadlineHours;
    }

    public void setCancellationDeadlineHours(int cancellationDeadlineHours) {
        this.cancellationDeadlineHours = cancellationDeadlineHours;
    }
}