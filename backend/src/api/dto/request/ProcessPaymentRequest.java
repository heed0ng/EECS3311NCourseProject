package backend.api.dto.request;

public class ProcessPaymentRequest {

    private String bookingId;
    private String savedPaymentMethodId;

    public ProcessPaymentRequest() {
    }

    public String getBookingId() {
        return this.bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getSavedPaymentMethodId() {
        return this.savedPaymentMethodId;
    }

    public void setSavedPaymentMethodId(String savedPaymentMethodId) {
        this.savedPaymentMethodId = savedPaymentMethodId;
    }
}