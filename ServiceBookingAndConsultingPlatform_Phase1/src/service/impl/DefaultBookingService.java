package service.impl;

import java.time.LocalDateTime;
import java.util.List;

import model.core.AvailabilitySlot;
import model.core.Booking;
import model.core.ConsultantServiceOffering;
import model.notification.BookingCancelledEvent;
import model.notification.BookingRequestedEvent;
import model.payment.PaymentTransaction;
import model.policy.NotificationPolicy;
import repository.AvailabilitySlotRepository;
import repository.BookingRepository;
import repository.ClientRepository;
import repository.ConsultantServiceOfferingRepository;
import repository.PaymentTransactionRepository;
import repository.PolicyRepository;
import repository.sqlite.IdGenerator;
import service.BookingService;
import observer.EventPublisher;
import state.RequestedState;
import util.BusinessRuleViolationException;
import util.EntityNotFoundException;
import util.PaymentMethodType;
import util.PaymentTransactionStatus;
import util.PaymentTransactionType;

public class DefaultBookingService implements BookingService {
	private final IdGenerator idGenerator;
    private final ClientRepository clientRepository;
    private final ConsultantServiceOfferingRepository offeringRepository;
    private final AvailabilitySlotRepository availabilitySlotRepository;
    private final BookingRepository bookingRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final PolicyRepository policyRepository;
    private final EventPublisher eventPublisher;

    public DefaultBookingService(ClientRepository clientRepository,
            ConsultantServiceOfferingRepository offeringRepository,
            AvailabilitySlotRepository availabilitySlotRepository,
            BookingRepository bookingRepository,
            PaymentTransactionRepository paymentTransactionRepository,
            PolicyRepository policyRepository,
            EventPublisher eventPublisher, IdGenerator idGenerator) {
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
        return offeringRepository.findAllActive();
    }

    @Override
    public Booking requestBooking(String clientId, String offeringId, String slotId) {
        var client = clientRepository.findById(clientId).orElseThrow(() -> new EntityNotFoundException("Client not found."));
        var offering = offeringRepository.findById(offeringId).orElseThrow(() -> new EntityNotFoundException("Offering not found."));
        var slot = availabilitySlotRepository.findById(slotId).orElseThrow(() -> new EntityNotFoundException("Availability slot not found."));
        if (!offering.getConsultant().isApproved()) throw new BusinessRuleViolationException("Cannot book a consultant who is not approved.");
        if (!offering.isActive()) throw new BusinessRuleViolationException("Offering is inactive.");
        if (!slot.isAvailable()) throw new BusinessRuleViolationException("Selected slot is not available.");
        if (!slot.isOwnedBy(offering.getConsultant().getUserId())) throw new BusinessRuleViolationException("Slot does not belong to the selected consultant.");

        LocalDateTime now = LocalDateTime.now();
        String bookingId = idGenerator.nextId("bookings", "booking_id", "booking");
        Booking booking = new Booking(bookingId, client, offering, slot, new RequestedState(), now, now, offering.getEffectivePrice());
        slot.setAvailable(false);
        bookingRepository.save(booking);
        availabilitySlotRepository.save(slot);
        publishRequested(booking);
        return booking;
    }

    @Override
    public Booking cancelBooking(String clientId, String bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new EntityNotFoundException("Booking not found."));
        if (!booking.belongsToClient(clientId)) throw new BusinessRuleViolationException("Booking does not belong to the client.");
        LocalDateTime now = LocalDateTime.now();
        var cancellationPolicy = policyRepository.getCancellationPolicy();
        var refundPolicy = policyRepository.getRefundPolicy();
        if (!cancellationPolicy.canCancel(booking, now)) throw new BusinessRuleViolationException("Cancellation deadline has passed.");
        double refundAmount = refundPolicy.calculateRefund(booking, now, cancellationPolicy);
        booking.cancel();
        bookingRepository.save(booking);
        booking.getSlot().setAvailable(true);
        availabilitySlotRepository.save(booking.getSlot());
        if (refundAmount > 0.0) {
        	String transactionId = idGenerator.nextId("payment_transactions", "transaction_id", "transaction");
            PaymentTransaction refund = new PaymentTransaction(transactionId, booking, booking.getClient(), PaymentTransactionType.REFUND, PaymentTransactionStatus.SUCCESS, PaymentMethodType.BANK_TRANSFER, refundAmount, now);
            paymentTransactionRepository.save(refund);
        }
        publishCancelled(booking, refundAmount);
        return booking;
    }

    @Override
    public List<Booking> getBookingHistory(String clientId) { return bookingRepository.findByClient(clientId); }
    @Override
    public List<Booking> getAllBookings() { return bookingRepository.findAll(); }
    @Override
    public List<AvailabilitySlot> getAllAvailableSlots() { return availabilitySlotRepository.findAllAvailable(); }

    @Override
    public String getCancellationSummary(String clientId, String bookingId) {
		Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new EntityNotFoundException("Booking not found."));
		if (!booking.belongsToClient(clientId)) throw new BusinessRuleViolationException("Booking does not belong to the client.");
		LocalDateTime now = LocalDateTime.now();
        var cancellationPolicy = policyRepository.getCancellationPolicy();
        var refundPolicy = policyRepository.getRefundPolicy();
        if (!cancellationPolicy.canCancel(booking, now)) return "Cancellation not allowed anymore.";
        double refund = refundPolicy.calculateRefund(booking, now, cancellationPolicy);
        return "Cancellation allowed. Current refund estimate: $" + String.format("%.2f", refund);
    }

    private void publishRequested(Booking booking) {
        NotificationPolicy policy = policyRepository.getNotificationPolicy();
        if (policy.isNotifyOnBookingRequested()) {
            eventPublisher.publish(new BookingRequestedEvent(eventPublisher.nextEventId(), LocalDateTime.now(), "Booking requested by " + booking.getClient().getName() + " for service " + booking.getOffering().getConsultingService().getName() + "."));
        }
    }

    private void publishCancelled(Booking booking, double refundAmount) {
        NotificationPolicy policy = policyRepository.getNotificationPolicy();
        if (policy.isNotifyOnBookingCancelled()) {
            eventPublisher.publish(new BookingCancelledEvent(eventPublisher.nextEventId(), LocalDateTime.now(), "Booking " + booking.getBookingId() + " was cancelled. Refund amount: $" + String.format("%.2f", refundAmount)));
        }
    }
}
