package backend.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import backend.model.core.AvailabilitySlot;
import backend.model.core.Booking;
import backend.model.core.ConsultantServiceOffering;
import backend.model.notification.BookingAcceptedEvent;
import backend.model.notification.BookingRejectedEvent;
import backend.model.payment.PaymentTransaction;
import backend.observer.EventPublisher;
import backend.repository.*;
import backend.repository.sqlite.IdGenerator;
import backend.service.ConsultantService;
import backend.util.*;

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
        var consultant = this.consultantRepository.findById(consultantId)
                .orElseThrow(() -> new EntityNotFoundException("Consultant not found."));

        if (!consultant.isApproved()) throw new AuthorizationException("Consultant is not approved.");
        if (!startDateTime.isBefore(endDateTime)) throw new BusinessRuleViolationException("Slot start time must be before end time.");

        if (!startDateTime.isAfter(LocalDateTime.now())) throw new BusinessRuleViolationException("Availability slot start time must be after than right now.");
        for (AvailabilitySlot slot : this.slotRepository.findByConsultant(consultantId)) {
            if (this.blocksAvailabilityOverlap(slot) && slot.overlaps(startDateTime, endDateTime)) throw new BusinessRuleViolationException("Overlapping slot detected.");
        }

        String slotId = this.idGenerator.nextId("availability_slots", "slot_id", "slot");
        AvailabilitySlot slot = new AvailabilitySlot(slotId, consultant, startDateTime, endDateTime, true);
        this.slotRepository.save(slot);
        return slot;
    }
    
    @Override
    public AvailabilitySlot removeAvailabilitySlot(String consultantId, String slotId) {
        AvailabilitySlot slot = this.slotRepository.findById(slotId)
                .orElseThrow(() -> new EntityNotFoundException("Availability slot not found."));

        if (!slot.isOwnedBy(consultantId)) throw new AuthorizationException("Availability slot does not belong to the consultant.");
        if (!slot.isAvailable()) throw new BusinessRuleViolationException("Only currently available slots can be removed.");
        if (this.bookingRepository.hasNonTerminalBookingForSlot(slotId)) throw new BusinessRuleViolationException("Availability slot cannot be removed because it is tied to an active booking.");

        slot.setAvailable(false);
        this.slotRepository.save(slot);
        return slot;
    }

    @Override
    public AvailabilitySlot updateAvailabilitySlot(String consultantId, String slotId, LocalDateTime startDateTime, LocalDateTime endDateTime) {

        AvailabilitySlot slot = this.slotRepository.findById(slotId).orElseThrow(() -> new EntityNotFoundException("Availability slot not found."));

        if (!slot.isOwnedBy(consultantId)) throw new AuthorizationException("Availability slot does not belong to the consultant.");
        if (!slot.isAvailable()) throw new BusinessRuleViolationException("Only currently available slots can be updated.");
        if (this.bookingRepository.hasNonTerminalBookingForSlot(slotId)) throw new BusinessRuleViolationException("Availability slot cannot be updated because it is tied to an active booking.");
        if (!startDateTime.isBefore(endDateTime))  throw new BusinessRuleViolationException("Slot start time must be before end time.");
        for (AvailabilitySlot otherSlot : this.slotRepository.findByConsultant(consultantId)) {
            if (otherSlot.getSlotId().equals(slotId)) continue;
            if (this.blocksAvailabilityOverlap(otherSlot) && otherSlot.overlaps(startDateTime, endDateTime)) {
                throw new BusinessRuleViolationException("Updated slot would overlap with another active slot.");
            }
        }
        if (!startDateTime.isAfter(LocalDateTime.now())) throw new BusinessRuleViolationException("Availability slot start time must be after than right now.");
       
        slot.setStartDateTime(startDateTime);
        slot.setEndDateTime(endDateTime);
        this.slotRepository.save(slot);
        return slot;
    }
    
    @Override
    public ConsultantServiceOffering addServiceOffering(String consultantId, String serviceId, Double customPrice) {
        var consultant = this.consultantRepository.findById(consultantId).orElseThrow(() -> new EntityNotFoundException("Consultant not found."));

        if (!consultant.isApproved()) throw new AuthorizationException("Consultant is not approved.");
        if (customPrice != null && customPrice < 0.0) throw new BusinessRuleViolationException("Custom price must be zero or greater.");
        var pricingPolicy = this.policyRepository.getPricingPolicy();
        if (!pricingPolicy.isAllowConsultantCustomPrice() && customPrice != null) throw new BusinessRuleViolationException("Custom price is blocked by administrator.");

        var service = this.consultingServiceRepository.findById(serviceId).orElseThrow(() -> new EntityNotFoundException("Consulting service not found."));

        boolean duplicateExists = this.offeringRepository.findByConsultant(consultantId).stream().anyMatch(existingOffering ->
                        existingOffering.isActive() && existingOffering.getConsultingService().getServiceId().equals(serviceId));

        if (duplicateExists) throw new BusinessRuleViolationException("Consultant already has an active offering for this consulting service.");

        String offeringId = this.idGenerator.nextId("consultant_service_offerings", "offering_id", "offering");
        ConsultantServiceOffering offering = new ConsultantServiceOffering(offeringId, consultant, service, customPrice, true);

        this.offeringRepository.save(offering);
        return offering;
    }
    
    @Override
    public ConsultantServiceOffering removeServiceOffering(String consultantId, String offeringId) {
        ConsultantServiceOffering offering = this.offeringRepository.findById(offeringId)
                .orElseThrow(() -> new EntityNotFoundException("Service offering not found."));

        if (offering.getConsultant() == null || offering.getConsultant().getUserId() == null
                || !offering.getConsultant().getUserId().equals(consultantId)) {
            throw new AuthorizationException("Service offering does not belong to the consultant.");
        }

        if (!offering.isActive()) throw new BusinessRuleViolationException("Service offering is already inactive.");

        offering.setActive(false);
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
                        " was accepted and moved to pending payment.", booking.getClient().getUserId(), booking.getOffering().getConsultant().getUserId(), null));
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
                    "Booking " + booking.getBookingId() + " was rejected by the consultant.", booking.getClient().getUserId(), booking.getOffering().getConsultant().getUserId(), null));
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
    
    private boolean blocksAvailabilityOverlap(AvailabilitySlot slot) {
        return slot.isAvailable() || this.bookingRepository.hasNonTerminalBookingForSlot(slot.getSlotId());
    }
    
    private void ensureOwner(String consultantId, Booking booking) {
        if (!booking.belongsToConsultant(consultantId)) throw new AuthorizationException("Booking does not belong to the consultant.");
    }

    private void createPendingPaymentTransactionIfMissing(Booking booking) {
        boolean alreadyExists = this.paymentTransactionRepository.findByBooking(booking.getBookingId()).stream().anyMatch(transaction ->
        	transaction.getTransactionType() == PaymentTransactionType.PAYMENT && transaction.getStatus() == PaymentTransactionStatus.PENDING);

        if (alreadyExists) return;

        String transactionId = idGenerator.nextId("payment_transactions", "transaction_id", "transaction");
        PaymentTransaction pendingTransaction = new PaymentTransaction(transactionId, booking, booking.getClient(), PaymentTransactionType.PAYMENT,
                PaymentTransactionStatus.PENDING, PaymentMethodType.NONE_SELECTED, booking.getPrice(), LocalDateTime.now());
        this.paymentTransactionRepository.save(pendingTransaction);
    }
}