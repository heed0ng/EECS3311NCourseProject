package backend.model.payment;

import java.time.LocalDateTime;

import backend.model.core.Booking;
import backend.model.user.Client;
import backend.util.PaymentMethodType;
import backend.util.PaymentTransactionStatus;
import backend.util.PaymentTransactionType;

public class PaymentTransaction {
    private final String transactionId;
    private final Booking booking;
    private final Client client;
    private final PaymentTransactionType transactionType;
    private PaymentTransactionStatus status;
    private final PaymentMethodType methodType;
    private final double amount;
    private final LocalDateTime createdAt;

    public PaymentTransaction(String transactionId, Booking booking, Client client, PaymentTransactionType transactionType, 
    		PaymentTransactionStatus status, PaymentMethodType methodType, double amount, LocalDateTime createdAt) {
        this.transactionId = transactionId;
        this.booking = booking;
        this.client = client;
        this.transactionType = transactionType;
        this.status = status;
        this.methodType = methodType;
        this.amount = amount;
        this.createdAt = createdAt;
    }

    public String getTransactionId() {
        return this.transactionId;
    }

    public Booking getBooking() {
        return this.booking;
    }

    public Client getClient() {
        return this.client;
    }

    public PaymentTransactionType getTransactionType() {
        return this.transactionType;
    }

    public PaymentTransactionStatus getStatus() {
        return this.status;
    }

    public void setStatus(PaymentTransactionStatus status) {
        this.status = status;
    }

    public PaymentMethodType getMethodType() {
        return this.methodType;
    }

    public double getAmount() {
        return this.amount;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }
}
