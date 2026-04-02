package service.impl;

import java.time.LocalDateTime;
import java.util.List;

import model.core.AvailabilitySlot;
import model.core.Booking;
import model.core.ConsultantServiceOffering;
import model.notification.BookingAcceptedEvent;
import model.notification.BookingRejectedEvent;
import model.payment.PaymentTransaction;
import observer.EventPublisher;
import repository.*;
import repository.sqlite.IdGenerator;
import service.ConsultantService;
import util.*;

public class DefaultConsultantService implements ConsultantService {
    private final IdGenerator idGenerator;
    private final ConsultantRepository consultantRepository;
    private final ConsultingServiceRepository consultingServiceRepository;
    private final ConsultantServiceOfferingRepository offeringRepository;
    private final AvailabilitySlotRepository slotRepository;
    private final BookingRepository bookingRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final PolicyRepository policyRepository;
    private final EventPublisher eventPublisher;

    public DefaultConsultantService(ConsultantRepository consultantRepository, ConsultingServiceRepository consultingServiceRepository,
            ConsultantServiceOfferingRepository offeringRepository, AvailabilitySlotRepository slotRepository, BookingRepository bookingRepository,
            PaymentTransactionRepository paymentTransactionRepository, PolicyRepository policyRepository, EventPublisher eventPublisher, IdGenerator idGenerator) {
        this.consultantRepository = consultantRepository;
        this.consultingServiceRepository = consultingServiceRepository;
        this.offeringRepository = offeringRepository;
        this.slotRepository = slotRepository;
        this.bookingRepository = bookingRepository;
        this.paymentTransactionRepository = paymentTransactionRepository;
        this.policyRepository = policyRepository;
        this.eventPublisher = eventPublisher;
        this.idGenerator = idGenerator;
    }

    @Override
    public AvailabilitySlot addAvailabilitySlot(String consultantId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        var consultant = this.consultantRepository.findById(consultantId).orElseThrow(() -> new EntityNotFoundException("Consultant not found."));
        if (!consultant.isApproved()) throw new AuthorizationException("Consultant is not approved.");
        if (!startDateTime.isBefore(endDateTime)) throw new BusinessRuleViolationException("Slot start time must be before end time.");
        for (AvailabilitySlot slot : this.slotRepository.findByConsultant(consultantId)) {
            if (slot.overlaps(startDateTime, endDateTime)) throw new BusinessRuleViolationException("Overlapping slot detected.");
        }

        String slotId = this.idGenerator.nextId("availability_slots", "slot_id", "slot");
        AvailabilitySlot slot = new AvailabilitySlot(slotId, consultant, startDateTime, endDateTime, true);
        this.slotRepository.save(slot);
        return slot;
    }

    @Override
    public ConsultantServiceOffering addServiceOffering(String consultantId, String serviceId, Double customPrice) {
        var consultant = this.consultantRepository.findById(consultantId).orElseThrow(() -> new EntityNotFoundException("Consultant not found."));
        if (!consultant.isApproved()) throw new AuthorizationException("Consultant is not approved.");
        var pricingPolicy = this.policyRepository.getPricingPolicy();
        if (!pricingPolicy.isAllowConsultantCustomPrice()) customPrice = null;
        var service = consultingServiceRepository.findById(serviceId).orElseThrow(() -> new EntityNotFoundException("Consulting service not found."));

        boolean duplicateExists = offeringRepository.findByConsultant(consultantId).stream().anyMatch(existingOffering ->
                        existingOffering.isActive() && existingOffering.getConsultingService().getServiceId().equals(serviceId));

        if (duplicateExists) throw new BusinessRuleViolationException("Consultant already has an active offering for this consulting service.");
        
        String offeringId = this.idGenerator.nextId("consultant_service_offerings", "offering_id", "offering");
        ConsultantServiceOffering offering = new ConsultantServiceOffering(offeringId, consultant, service, customPrice, true);
        this.offeringRepository.save(offering);
        return offering;
    }

    @Override
    public List<AvailabilitySlot> getAvailabilitySlots(String consultantId) {
        return slotRepository.findByConsultant(consultantId);
    }

    @Override
    public List<Booking> getPendingBookingRequests(String consultantId) {
        return bookingRepository.findPendingRequestsForConsultant(consultantId);
    }

    @Override
    public Booking acceptBookingRequest(String consultantId, String bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new EntityNotFoundException("Booking not found."));
        this.ensureOwner(consultantId, booking);

        booking.confirm();
        booking.moveToPendingPayment();
        this.bookingRepository.save(booking);

        this.createPendingPaymentTransactionIfMissing(booking);

        if (policyRepository.getNotificationPolicy().isNotifyOnBookingAccepted()) {
            this.eventPublisher.publish(
                new BookingAcceptedEvent(eventPublisher.nextEventId(), LocalDateTime.now(),"Booking " + booking.getBookingId() + 
                		" was accepted and moved to pending payment."));
        }
        return booking;
    }

    @Override
    public Booking rejectBookingRequest(String consultantId, String bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new EntityNotFoundException("Booking not found."));
        this.ensureOwner(consultantId, booking);
        booking.reject();
        this.bookingRepository.save(booking);
        booking.getSlot().setAvailable(true);
        this.slotRepository.save(booking.getSlot());

        if (policyRepository.getNotificationPolicy().isNotifyOnBookingRejected()) {
            eventPublisher.publish(new BookingRejectedEvent(eventPublisher.nextEventId(), LocalDateTime.now(),
                    "Booking " + booking.getBookingId() + " was rejected by the consultant."));
        }
        return booking;
    }

    @Override
    public Booking completeBooking(String consultantId, String bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new EntityNotFoundException("Booking not found."));
        this.ensureOwner(consultantId, booking);
        booking.complete();
        this.bookingRepository.save(booking);
        return booking;
    }

// Because this is simulation below check is intentionally removed. In the demo, I will make this to be enabled instead of above, testing focused completeBooking()    
//    @Override
//    public Booking completeBooking(String consultantId, String bookingId) {
//        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new EntityNotFoundException("Booking not found."));
//        this.ensureOwner(consultantId, booking);
//
//        LocalDateTime now = LocalDateTime.now();
//        LocalDateTime slotEnd = booking.getSlot().getEndDateTime();
//
//        if (now.isBefore(slotEnd)) throw new BusinessRuleViolationException("Booking cannot be completed before the scheduled session has ended.");
//
//        booking.complete();
//        this.bookingRepository.save(booking);
//        return booking;
//    }
//    
    
    private void ensureOwner(String consultantId, Booking booking) {
        if (!booking.belongsToConsultant(consultantId)) throw new AuthorizationException("Booking does not belong to the consultant.");
    }

    private void createPendingPaymentTransactionIfMissing(Booking booking) {
        boolean alreadyExists = this.paymentTransactionRepository.findByBooking(booking.getBookingId()).stream().anyMatch(transaction ->
        	transaction.getTransactionType() == PaymentTransactionType.PAYMENT && transaction.getStatus() == PaymentTransactionStatus.PENDING);

        if (alreadyExists) return;

        String transactionId = idGenerator.nextId("payment_transactions", "transaction_id", "transaction");
        PaymentTransaction pendingTransaction = new PaymentTransaction(transactionId, booking, booking.getClient(), PaymentTransactionType.PAYMENT,
                PaymentTransactionStatus.PENDING, PaymentMethodType.NONE_SELECTED, booking.getAgreedPrice(), LocalDateTime.now());
        this.paymentTransactionRepository.save(pendingTransaction);
    }
}