package backend.api.dto.response;

public class PaymentTransactionResponse {

    private String paymentTransactionId;
    private String bookingId;
    private String paymentMethodType;
    private Double amount;
    private String paymentStatus;
    private String transactionType;
    private String processedAt;
    private String message;

    public PaymentTransactionResponse() {
    }

    public PaymentTransactionResponse(
            String paymentTransactionId,
            String bookingId,
            String paymentMethodType,
            Double amount,
            String paymentStatus,
            String transactionType,
            String processedAt,
            String message) {
        this.paymentTransactionId = paymentTransactionId;
        this.bookingId = bookingId;
        this.paymentMethodType = paymentMethodType;
        this.amount = amount;
        this.paymentStatus = paymentStatus;
        this.transactionType = transactionType;
        this.processedAt = processedAt;
        this.message = message;
    }

    public String getPaymentTransactionId() {
        return this.paymentTransactionId;
    }

    public void setPaymentTransactionId(String paymentTransactionId) {
        this.paymentTransactionId = paymentTransactionId;
    }

    public String getBookingId() {
        return this.bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getPaymentMethodType() {
        return this.paymentMethodType;
    }

    public void setPaymentMethodType(String paymentMethodType) {
        this.paymentMethodType = paymentMethodType;
    }

    public Double getAmount() {
        return this.amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getPaymentStatus() {
        return this.paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getTransactionType() {
        return this.transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getProcessedAt() {
        return this.processedAt;
    }

    public void setProcessedAt(String processedAt) {
        this.processedAt = processedAt;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}