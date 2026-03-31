package service.impl;

import java.time.LocalDateTime;
import java.util.List;

import model.core.AvailabilitySlot;
import model.core.Booking;
import model.core.ConsultantServiceOffering;
import model.notification.BookingAcceptedEvent;
import model.notification.BookingRejectedEvent;
import observer.EventPublisher;
import repository.AvailabilitySlotRepository;
import repository.BookingRepository;
import repository.ConsultantRepository;
import repository.ConsultantServiceOfferingRepository;
import repository.ConsultingServiceRepository;
import repository.PolicyRepository;
import repository.sqlite.IdGenerator;
import service.ConsultantService;
import util.AuthorizationException;
import util.BusinessRuleViolationException;
import util.EntityNotFoundException;

public class DefaultConsultantService implements ConsultantService {
	private final IdGenerator idGenerator;
    private final ConsultantRepository consultantRepository;
    private final ConsultingServiceRepository consultingServiceRepository;
    private final ConsultantServiceOfferingRepository offeringRepository;
    private final AvailabilitySlotRepository slotRepository;
    private final BookingRepository bookingRepository;
    private final PolicyRepository policyRepository;
    private final EventPublisher eventPublisher;

    public DefaultConsultantService(ConsultantRepository consultantRepository, ConsultingServiceRepository consultingServiceRepository,
            ConsultantServiceOfferingRepository offeringRepository, AvailabilitySlotRepository slotRepository,
            BookingRepository bookingRepository, PolicyRepository policyRepository, EventPublisher eventPublisher, IdGenerator idGenerator) {
        this.consultantRepository = consultantRepository;
        this.consultingServiceRepository = consultingServiceRepository;
        this.offeringRepository = offeringRepository;
        this.slotRepository = slotRepository;
        this.bookingRepository = bookingRepository;
        this.policyRepository = policyRepository;
        this.eventPublisher = eventPublisher;
        this.idGenerator = idGenerator;
    }

    @Override
    public AvailabilitySlot addAvailabilitySlot(String consultantId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        var consultant = consultantRepository.findById(consultantId).orElseThrow(() -> new EntityNotFoundException("Consultant not found."));
        if (!consultant.isApproved()) throw new AuthorizationException("Consultant is not approved.");
        if (!startDateTime.isBefore(endDateTime)) throw new BusinessRuleViolationException("Slot start time must be before end time.");
        for (AvailabilitySlot slot : slotRepository.findByConsultant(consultantId)) {
            if (slot.overlaps(startDateTime, endDateTime)) throw new BusinessRuleViolationException("Overlapping slot detected.");
        }
        
        String slotId = this.idGenerator.nextId("availability_slots", "slot_id", "slot");
        AvailabilitySlot slot = new AvailabilitySlot(slotId, consultant, startDateTime, endDateTime, true);
        slotRepository.save(slot);
        return slot;
    }

    @Override
    public ConsultantServiceOffering addServiceOffering(String consultantId, String serviceId, Double customPrice) {
        var consultant = consultantRepository.findById(consultantId).orElseThrow(() -> new EntityNotFoundException("Consultant not found."));
        if (!consultant.isApproved()) throw new AuthorizationException("Consultant is not approved.");
        var pricingPolicy = policyRepository.getPricingPolicy();
        if (!pricingPolicy.isAllowConsultantCustomPrice()) customPrice = null;
        var service = consultingServiceRepository.findById(serviceId).orElseThrow(() -> new EntityNotFoundException("Consulting service not found."));
        
        String offeringId = this.idGenerator.nextId("consultant_service_offerings", "offering_id", "offering");
        ConsultantServiceOffering offering = new ConsultantServiceOffering(offeringId, consultant, service, customPrice, true);
        offeringRepository.save(offering);
        return offering;
    }

    @Override
    public List<AvailabilitySlot> getAvailabilitySlots(String consultantId) { return slotRepository.findByConsultant(consultantId); }
    @Override
    public List<Booking> getPendingBookingRequests(String consultantId) { return bookingRepository.findPendingRequestsForConsultant(consultantId); }

    @Override
    public Booking acceptBookingRequest(String consultantId, String bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new EntityNotFoundException("Booking not found."));
        ensureOwner(consultantId, booking);
        booking.confirm();
        booking.moveToPendingPayment();
        bookingRepository.save(booking);
        if (policyRepository.getNotificationPolicy().isNotifyOnBookingAccepted()) {
            eventPublisher.publish(new BookingAcceptedEvent(eventPublisher.nextEventId(), LocalDateTime.now(), "Booking " + booking.getBookingId() + " was accepted and moved to pending payment."));
        }
        return booking;
    }
    
    @Override
    public Booking rejectBookingRequest(String consultantId, String bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new EntityNotFoundException("Booking not found."));
        ensureOwner(consultantId, booking);
        booking.reject();
        bookingRepository.save(booking);
        booking.getSlot().setAvailable(true);
        slotRepository.save(booking.getSlot());
        if (policyRepository.getNotificationPolicy().isNotifyOnBookingRejected()) {
            eventPublisher.publish(new BookingRejectedEvent(eventPublisher.nextEventId(), LocalDateTime.now(), "Booking " + booking.getBookingId() + " was rejected by the consultant."));
        }
        return booking;
    }

    @Override
    public Booking completeBooking(String consultantId, String bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new EntityNotFoundException("Booking not found."));
        ensureOwner(consultantId, booking);
        booking.complete();
        bookingRepository.save(booking);
        return booking;
    }

    private void ensureOwner(String consultantId, Booking booking) {
        if (!booking.belongsToConsultant(consultantId)) throw new AuthorizationException("Booking does not belong to the consultant.");
    }
}
