package backend.api.dto.request;

public class UpdateSavedPaymentMethodRequest {

    private String nickname;
    private String paymentDetails;
    private String paymentMetadata;

    public UpdateSavedPaymentMethodRequest() {
    }

    public String getNickname() {
        return this.nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPaymentDetails() {
        return this.paymentDetails;
    }

    public void setPaymentDetails(String paymentDetails) {
        this.paymentDetails = paymentDetails;
    }

    public String getPaymentMetadata() {
        return this.paymentMetadata;
    }

    public void setPaymentMetadata(String paymentMetadata) {
        this.paymentMetadata = paymentMetadata;
    }
}