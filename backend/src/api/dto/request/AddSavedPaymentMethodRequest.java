package backend.api.dto.request;

public class AddSavedPaymentMethodRequest {

    private String paymentMethodType;
    private String nickname;
    private String paymentDetails;
    private String paymentMetadata;

    public AddSavedPaymentMethodRequest() {
    }

    public String getPaymentMethodType() {
        return this.paymentMethodType;
    }

    public void setPaymentMethodType(String paymentMethodType) {
        this.paymentMethodType = paymentMethodType;
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