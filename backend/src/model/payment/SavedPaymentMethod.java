package backend.model.payment;

import backend.model.user.Client;
import backend.util.PaymentMethodType;

public class SavedPaymentMethod {
    private final String savedMethodId;
    private final Client client;
    private PaymentMethodType methodType;
    private String displayLabel;
    private String paymentDetails;
    private String paymentDetailData;

    public SavedPaymentMethod(String savedMethodId, Client client, PaymentMethodType methodType, String displayLabel, 
    		String paymentDetails, String paymentDetailData) {
        this.savedMethodId = savedMethodId;
        this.client = client;
        this.methodType = methodType;
        this.displayLabel = displayLabel;
        this.paymentDetails = paymentDetails;
        this.paymentDetailData = paymentDetailData;
    }

    public String getSavedMethodId() {
        return this.savedMethodId;
    }

    public Client getClient() {
        return this.client;
    }

    public PaymentMethodType getMethodType() {
        return this.methodType;
    }

    public void setMethodType(PaymentMethodType methodType) {
        this.methodType = methodType;
    }

    public String getDisplayLabel() {
        return this.displayLabel;
    }

    public void setDisplayLabel(String displayLabel) {
        this.displayLabel = displayLabel;
    }

    public String getPaymentDetails() {
        return this.paymentDetails;
    }

    public void setPaymentDetails(String paymentDetails) {
        this.paymentDetails = paymentDetails;
    }

    public String getPaymentDetailData() {
        return this.paymentDetailData;
    }

    public void setPaymentDetailData(String paymentDetailData) {
        this.paymentDetailData = paymentDetailData;
    }
    
    public String getMaskedPaymentDetails() {
        if (this.paymentDetails == null || this.paymentDetails.isBlank()) return "(empty)";

        switch (this.methodType) {
            case CREDIT_CARD:
            case DEBIT_CARD: {
                String digitsOnly = this.paymentDetails.replaceAll("\\D", "");
                if (digitsOnly.length() >= 4) return "**** **** **** " + digitsOnly.substring(digitsOnly.length() - 4);
                return "****";
            }
            case PAYPAL: return maskEmail(this.paymentDetails);
            case BANK_TRANSFER: {
                String digitsOnly = this.paymentDetails.replaceAll("\\D", "");
                if (digitsOnly.length() >= 4) return "******" + digitsOnly.substring(digitsOnly.length() - 4);
                return "******";
            }
            default: return "****";
        }
    }

    private String maskEmail(String email) {
        int atIndex = email.indexOf("@");
        if (atIndex <= 1) return "***" + email.substring(Math.max(0, atIndex));
        return email.charAt(0) + "***" + email.substring(atIndex);
    }

    @Override
    public String toString() {
        return "SavedPaymentMethod [savedMethodId=" + savedMethodId
                + ", client=" + client.getUserId()
                + ", methodType=" + methodType
                + ", displayLabel=" + displayLabel
                + ", paymentDetails=" + getMaskedPaymentDetails() + "]";
    }
}
