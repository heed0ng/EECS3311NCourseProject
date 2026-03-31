package model.core;

import java.time.LocalDateTime;

import model.user.Client;
import state.BookingState;

public class Booking {
    private final String bookingId;
    private final Client client;
    private final ConsultantServiceOffering offering;
    private final AvailabilitySlot slot;
    private BookingState state;
    private final LocalDateTime createdAt;
    private LocalDateTime lastUpdatedAt;
    private final double agreedPrice;

    public Booking(String bookingId, Client client, ConsultantServiceOffering offering, AvailabilitySlot slot,
            BookingState initialState, LocalDateTime createdAt, LocalDateTime lastUpdatedAt, double agreedPrice) {
        this.bookingId = bookingId;
        this.client = client;
        this.offering = offering;
        this.slot = slot;
        this.state = initialState;
        this.createdAt = createdAt;
        this.lastUpdatedAt = lastUpdatedAt;
        this.agreedPrice = agreedPrice;
    }

    public String getBookingId() { return bookingId; }
    public Client getClient() { return client; }
    public ConsultantServiceOffering getOffering() { return offering; }
    public AvailabilitySlot getSlot() { return slot; }
    public BookingState getState() { return state; }
    public String getStateName() { return state.getName(); }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getLastUpdatedAt() { return lastUpdatedAt; }
    public double getAgreedPrice() { return agreedPrice; }

    public void setState(BookingState state) {
        this.state = state;
        touch();
    }

    public void touch() { this.lastUpdatedAt = LocalDateTime.now(); }
    public void confirm() { this.state.confirm(this); }
    public void moveToPendingPayment() { this.state.moveToPendingPayment(this); }
    public void markPaid() { this.state.markPaid(this); }
    public void reject() { this.state.reject(this); }
    public void cancel() { this.state.cancel(this); }
    public void complete() { this.state.complete(this); }

    public boolean belongsToClient(String clientId) {
        return this.client != null && this.client.getUserId().equals(clientId);
    }

    public boolean belongsToConsultant(String consultantId) {
        return this.offering != null && this.offering.getConsultant() != null
                && this.offering.getConsultant().getUserId().equals(consultantId);
    }
}
