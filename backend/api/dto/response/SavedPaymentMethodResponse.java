package backend.api.dto.response;

public class SavedPaymentMethodResponse {

    private String savedPaymentMethodId;
    private String paymentMethodType;
    private String nickname;
    private String maskedDisplayValue;

    public SavedPaymentMethodResponse() {
    }

    public SavedPaymentMethodResponse(
            String savedPaymentMethodId,
            String paymentMethodType,
            String nickname,
            String maskedDisplayValue) {
        this.savedPaymentMethodId = savedPaymentMethodId;
        this.paymentMethodType = paymentMethodType;
        this.nickname = nickname;
        this.maskedDisplayValue = maskedDisplayValue;
    }

    public String getSavedPaymentMethodId() {
        return this.savedPaymentMethodId;
    }

    public void setSavedPaymentMethodId(String savedPaymentMethodId) {
        this.savedPaymentMethodId = savedPaymentMethodId;
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

    public String getMaskedDisplayValue() {
        return this.maskedDisplayValue;
    }

    public void setMaskedDisplayValue(String maskedDisplayValue) {
        this.maskedDisplayValue = maskedDisplayValue;
    }
}