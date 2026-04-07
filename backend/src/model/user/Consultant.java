package backend.model.user;

import backend.util.ConsultantApprovalStatus;

public class Consultant extends User {
    private ConsultantApprovalStatus approvalStatus;

    public Consultant(String userId, String name, String email, ConsultantApprovalStatus approvalStatus) {
        super(userId, name, email);
        this.approvalStatus = approvalStatus;
    }

    public ConsultantApprovalStatus getApprovalStatus() {
        return this.approvalStatus;
    }

    public void setApprovalStatus(ConsultantApprovalStatus approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public boolean isApproved() {
        return ConsultantApprovalStatus.APPROVED.equals(this.approvalStatus);
    }
}
