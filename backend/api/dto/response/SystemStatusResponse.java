package backend.api.dto.response;

public class SystemStatusResponse {
    private Integer pendingConsultantCount;
    private Integer requestedBookingCount;
    private Integer pendingPaymentCount;
    private Integer paidBookingCount;
    private Integer completedBookingCount;
    private Integer totalPaymentCount;

    public SystemStatusResponse() {
    }

    public SystemStatusResponse(
            Integer pendingConsultantCount,
            Integer requestedBookingCount,
            Integer pendingPaymentCount,
            Integer paidBookingCount,
            Integer completedBookingCount,
            Integer totalPaymentCount) {
        this.pendingConsultantCount = pendingConsultantCount;
        this.requestedBookingCount = requestedBookingCount;
        this.pendingPaymentCount = pendingPaymentCount;
        this.paidBookingCount = paidBookingCount;
        this.completedBookingCount = completedBookingCount;
        this.totalPaymentCount = totalPaymentCount;
    }

    public Integer getPendingConsultantCount() {
        return this.pendingConsultantCount;
    }

    public void setPendingConsultantCount(Integer pendingConsultantCount) {
        this.pendingConsultantCount = pendingConsultantCount;
    }

    public Integer getRequestedBookingCount() {
        return this.requestedBookingCount;
    }

    public void setRequestedBookingCount(Integer requestedBookingCount) {
        this.requestedBookingCount = requestedBookingCount;
    }

    public Integer getPendingPaymentCount() {
        return this.pendingPaymentCount;
    }

    public void setPendingPaymentCount(Integer pendingPaymentCount) {
        this.pendingPaymentCount = pendingPaymentCount;
    }

    public Integer getPaidBookingCount() {
        return this.paidBookingCount;
    }

    public void setPaidBookingCount(Integer paidBookingCount) {
        this.paidBookingCount = paidBookingCount;
    }

    public Integer getCompletedBookingCount() {
        return this.completedBookingCount;
    }

    public void setCompletedBookingCount(Integer completedBookingCount) {
        this.completedBookingCount = completedBookingCount;
    }

    public Integer getTotalPaymentCount() {
        return this.totalPaymentCount;
    }

    public void setTotalPaymentCount(Integer totalPaymentCount) {
        this.totalPaymentCount = totalPaymentCount;
    }
}