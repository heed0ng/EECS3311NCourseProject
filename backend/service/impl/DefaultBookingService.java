package backend.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import backend.model.core.AvailabilitySlot;
import backend.model.core.Booking;
import backend.model.core.ConsultantServiceOffering;
import backend.model.notification.BookingCancelledEvent;
import backend.model.notification.BookingRequestedEvent;
import backend.model.payment.PaymentTransaction;
import backend.model.policy.CancellationPolicy;
import backend.model.policy.NotificationPolicy;
import backend.model.policy.RefundPolicy;
import backend.observer.EventPublisher;
import backend.repository.AvailabilitySlotRepository;
import backend.repository.BookingRepository;
import backend.repository.ClientRepository;
import backend.repository.ConsultantServiceOfferingRepository;
import backend.repository.PaymentTransactionRepository;
import backend.repository.PolicyRepository;
import backend.repository.sqlite.IdGenerator;
import backend.service.BookingService;
import backend.state.RequestedState;
import backend.util.BusinessRuleViolationException;
import backend.util.EntityNotFoundException;
import backend.util.PaymentTransactionStatus;
import backend.util.PaymentTransactionType;

public class DefaultBookingService implements BookingService {
    private final IdGenerator idGenerator;
    private final ClientRepository clientRepository;
    private final ConsultantServiceOfferingRepository offeringRepository;
    private final AvailabilitySlotRepository availabilitySlotRepository;
    private final BookingRepository bookingRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final PolicyRepository policyRepository;
    private final EventPublisher eventPublisher;

    public DefaultBookingService(ClientRepository clientRepository, ConsultantServiceOfferingRepository offeringRepository,
            AvailabilitySlotRepository availabilitySlotRepository, BookingRepository bookingRepository, PaymentTransactionRepository paymentTransactionRepository, 
            PolicyRepository policyRepository, EventPublisher eventPublisher, IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
        this.clientRepository = clientRepository;
        this.offeringRepository = offeringRepository;
        this.availabilitySlotRepository = availabilitySlotRepository;
        this.bookingRepository = bookingRepository;
        this.paymentTransactionRepository = paymentTransactionRepository;
        this.policyRepository = policyRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public List<ConsultantServiceOffering> browseAvailableOfferings() {
        return this.offeringRepository.findAllActive();
    }

    @Override
    public Booking requestBooking(String clientId, String offeringId, String slotId) {
        var client = this.clientRepository.findById(clientId).orElseThrow(() -> new EntityNotFoundException("Client not found."));
        var offering = this.offeringRepository.findById(offeringId).orElseThrow(() -> new EntityNotFoundException("Offering not found."));
        var slot = this.availabilitySlotRepository.findById(slotId).orElseThrow(() -> new EntityNotFoundException("Availability slot not found."));

        if (!offering.getConsultant().isApproved()) throw new BusinessRuleViolationException("Cannot book a consultant who is not approved.");
        if (!offering.isActive()) throw new BusinessRuleViolationException("Offering is inactive.");
        if (!slot.isAvailable()) throw new BusinessRuleViolationException("Selected slot is not available.");
        if (!slot.isOwnedBy(offering.getConsultant().getUserId())) throw new BusinessRuleViolationException("Slot does not belong to the selected consultant.");

        LocalDateTime now = LocalDateTime.now();
        String bookingId = this.idGenerator.nextId("bookings", "booking_id", "booking");

        Booking booking = new Booking(bookingId, client, offering, slot, new RequestedState(), now, now, offering.getEffectivePrice());
        
        slot.setAvailable(false);
        this.bookingRepository.save(booking);
        this.availabilitySlotRepository.save(slot);
        this.publishRequested(booking);
        return booking;
    }

    @Override
    public Booking cancelBooking(String clientId, String bookingId) {
        Booking booking = this.bookingRepository.findById(bookingId).orElseThrow(() -> new EntityNotFoundException("Booking not found."));

        if (!booking.belongsToClient(clientId)) throw new BusinessRuleViolationException("Booking does not belong to the client.");

        this.ensureClientCancellationStateAllowed(booking);

        LocalDateTime now = LocalDateTime.now();
        CancellationPolicy cancellationPolicy = this.policyRepository.getCancellationPolicy();
        RefundPolicy refundPolicy = this.policyRepository.getRefundPolicy();

        if (!cancellationPolicy.canCancel(booking, now)) throw new BusinessRuleViolationException("Cancellation deadline has passed.");
        
        List<PaymentTransaction> bookingTransactions = this.paymentTransactionRepository.findByBooking(bookingId);
        PaymentTransaction successfulPaymentTransaction = this.findSuccessfulPaymentTransaction(bookingTransactions);
        PaymentTransaction pendingPaymentTransaction = this.findPendingPaymentTransaction(bookingTransactions);

        double refundAmount = 0.0;
        if (successfulPaymentTransaction != null) refundAmount = refundPolicy.calculateRefund(booking, now, cancellationPolicy);

        booking.cancel();
        this.bookingRepository.save(booking);
        booking.getSlot().setAvailable(true);
        this.availabilitySlotRepository.save(booking.getSlot());

        if (successfulPaymentTransaction != null && refundAmount > 0.0) {
            String transactionId = this.idGenerator.nextId("payment_transactions", "transaction_id", "transaction");

            PaymentTransaction refundTransaction = new PaymentTransaction(transactionId, booking, booking.getClient(),
                    PaymentTransactionType.REFUND, PaymentTransactionStatus.SUCCESS, 
                    successfulPaymentTransaction.getMethodType(), refundAmount, now);

            this.paymentTransactionRepository.save(refundTransaction);

        } else if (successfulPaymentTransaction == null && pendingPaymentTransaction != null) {
            PaymentTransaction failedPendingPaymentTransaction = new PaymentTransaction(pendingPaymentTransaction.getTransactionId(),
                    booking, booking.getClient(), PaymentTransactionType.PAYMENT, PaymentTransactionStatus.FAILED,
                    pendingPaymentTransaction.getMethodType(), pendingPaymentTransaction.getAmount(), pendingPaymentTransaction.getCreatedAt());

            this.paymentTransactionRepository.save(failedPendingPaymentTransaction);
        }

        this.publishCancelled(booking, refundAmount);
        return booking;
    }

    @Override
    public List<Booking> getBookingHistory(String clientId) {
        return this.bookingRepository.findByClient(clientId);
    }

    @Override
    public List<Booking> getAllBookings() {
        return this.bookingRepository.findAll();
    }

    @Override
    public List<AvailabilitySlot> getAllAvailableSlots() {
        return this.availabilitySlotRepository.findAllAvailable();
    }

    @Override
    public String getCancellationSummary(String clientId, String bookingId) {
        Booking booking = this.bookingRepository.findById(bookingId).orElseThrow(() -> new EntityNotFoundException("Booking not found."));

        if (!booking.belongsToClient(clientId)) throw new BusinessRuleViolationException("Booking does not belong to the client.");
        String stateSpecificMessage = this.buildNonCancellableStateSummaryMessage(booking);
        if (stateSpecificMessage != null) return stateSpecificMessage;

        LocalDateTime now = LocalDateTime.now();
        CancellationPolicy cancellationPolicy = this.policyRepository.getCancellationPolicy();
        RefundPolicy refundPolicy = this.policyRepository.getRefundPolicy();

        if (!cancellationPolicy.canCancel(booking, now)) return "Cancellation not allowed anymore.";

        List<PaymentTransaction> bookingTransactions = this.paymentTransactionRepository.findByBooking(bookingId);

        PaymentTransaction successfulPaymentTransaction =  this.findSuccessfulPaymentTransaction(bookingTransactions);

        if (successfulPaymentTransaction == null) return "Cancellation allowed. No refund applies because no successful payment exists yet.";
        double refund = refundPolicy.calculateRefund(booking, now, cancellationPolicy);
        
        return "Cancellation allowed. Current refund estimate: $" + String.format("%.2f", refund);
    }

    private PaymentTransaction findSuccessfulPaymentTransaction(List<PaymentTransaction> transactions) {
        return transactions.stream().filter(transaction ->
                        transaction.getTransactionType() == PaymentTransactionType.PAYMENT
                                && transaction.getStatus() == PaymentTransactionStatus.SUCCESS).findFirst().orElse(null);
    }

    private PaymentTransaction findPendingPaymentTransaction(List<PaymentTransaction> transactions) {
        return transactions.stream().filter(transaction ->
                        transaction.getTransactionType() == PaymentTransactionType.PAYMENT
                                && transaction.getStatus() == PaymentTransactionStatus.PENDING).findFirst().orElse(null);
    }

    private void ensureClientCancellationStateAllowed(Booking booking) {
        String stateName = booking.getStateName();

        if (!"Requested".equals(stateName) && !"Confirmed".equals(stateName) && !"Pending Payment".equals(stateName) && !"Paid".equals(stateName)) {
            throw new BusinessRuleViolationException("Cannot cancel when booking is in state " + stateName + ".");
        }
    }

    private String buildNonCancellableStateSummaryMessage(Booking booking) {
        String stateName = booking.getStateName();

        if ("Cancelled".equals(stateName)) return "Booking is already cancelled.";
        if ("Rejected".equals(stateName)) return "Booking was already rejected by the consultant.";
        if ("Completed".equals(stateName)) return "Booking is already completed and can no longer be cancelled.";
        if (!"Requested".equals(stateName) && !"Confirmed".equals(stateName) && !"Pending Payment".equals(stateName) && !"Paid".equals(stateName)) {
            return "Cancellation is not available when booking is in state " + stateName + ".";
        }
        return null;
    }

    private void publishRequested(Booking booking) {
        NotificationPolicy policy = this.policyRepository.getNotificationPolicy();
        if (policy.isNotifyOnBookingRequested()) {
            this.eventPublisher.publish(new BookingRequestedEvent(this.eventPublisher.nextEventId(), LocalDateTime.now(),
                            "Booking requested by " + booking.getClient().getName() + " for service " + booking.getOffering().getConsultingService().getName()+ "."));
        }
    }

    private void publishCancelled(Booking booking, double refundAmount) {
        NotificationPolicy policy = this.policyRepository.getNotificationPolicy();
        if (policy.isNotifyOnBookingCancelled()) {
            this.eventPublisher.publish(new BookingCancelledEvent(this.eventPublisher.nextEventId(), LocalDateTime.now(),
                            "Booking " + booking.getBookingId() + " was cancelled. Refund amount: $" + String.format("%.2f", refundAmount)));
        }
    }
}