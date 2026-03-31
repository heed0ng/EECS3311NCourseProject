package service.impl;

import java.time.LocalDateTime;
import java.util.List;

import model.notification.ConsultantApprovalEvent;
import model.policy.*;
import model.user.Consultant;
import observer.EventPublisher;
import repository.ConsultantRepository;
import repository.PolicyRepository;
import service.AdminService;
import util.AuthorizationException;
import util.ConsultantApprovalStatus;
import util.EntityNotFoundException;

public class DefaultAdminService implements AdminService {
    private final ConsultantRepository consultantRepository;
    private final PolicyRepository policyRepository;
    private final EventPublisher eventPublisher;

    public DefaultAdminService(ConsultantRepository consultantRepository, PolicyRepository policyRepository, EventPublisher eventPublisher) {
        this.consultantRepository = consultantRepository;
        this.policyRepository = policyRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public List<Consultant> getPendingConsultants() { return consultantRepository.findPendingApproval(); }

    @Override
    public Consultant approveConsultant(String adminId, String consultantId) {
    	this.ensureValidAdmin(adminId);
        Consultant consultant = consultantRepository.findById(consultantId).orElseThrow(() -> new EntityNotFoundException("Consultant not found."));
        consultant.setApprovalStatus(ConsultantApprovalStatus.APPROVED);
        consultantRepository.save(consultant);
        publishDecision(consultant, true);
        return consultant;
    }

    @Override
    public Consultant rejectConsultant(String adminId, String consultantId) {
    	this.ensureValidAdmin(adminId);
        Consultant consultant = consultantRepository.findById(consultantId).orElseThrow(() -> new EntityNotFoundException("Consultant not found."));
        consultant.setApprovalStatus(ConsultantApprovalStatus.REJECTED);
        consultantRepository.save(consultant);
        publishDecision(consultant, false);
        return consultant;
    }

    @Override
    public CancellationPolicy updateCancellationPolicy(String adminId, int cancellationDeadlineHours) {
    	this.ensureValidAdmin(adminId);
        CancellationPolicy policy = policyRepository.getCancellationPolicy();
        policy.setCancellationDeadlineHours(cancellationDeadlineHours);
        policyRepository.saveCancellationPolicy(policy);
        return policy;
    }

    @Override
    public RefundPolicy updateRefundPolicy(String adminId, double refundPercentBeforeDeadline, double refundPercentAfterDeadline) {
    	this.ensureValidAdmin(adminId);
    	RefundPolicy policy = policyRepository.getRefundPolicy();
        policy.setRefundPercentBeforeDeadline(refundPercentBeforeDeadline);
        policy.setRefundPercentAfterDeadline(refundPercentAfterDeadline);
        policyRepository.saveRefundPolicy(policy);
        return policy;
    }

    @Override
    public PricingPolicy updatePricingPolicy(String adminId, boolean allowConsultantCustomPrice) {
    	this.ensureValidAdmin(adminId);
    	PricingPolicy policy = policyRepository.getPricingPolicy();
        policy.setAllowConsultantCustomPrice(allowConsultantCustomPrice);
        policyRepository.savePricingPolicy(policy);
        return policy;
    }

    @Override
    public NotificationPolicy updateNotificationPolicy(String adminId, boolean notifyOnBookingRequested,
            boolean notifyOnBookingAccepted, boolean notifyOnBookingRejected,boolean notifyOnPaymentProcessed, 
            boolean notifyOnBookingCancelled, boolean notifyOnConsultantApprovalDecision) {
    	this.ensureValidAdmin(adminId);
        NotificationPolicy policy = policyRepository.getNotificationPolicy();
        policy.setNotifyOnBookingRequested(notifyOnBookingRequested);
        policy.setNotifyOnBookingAccepted(notifyOnBookingAccepted);
        policy.setNotifyOnBookingRejected(notifyOnBookingRejected);
        policy.setNotifyOnPaymentProcessed(notifyOnPaymentProcessed);
        policy.setNotifyOnBookingCancelled(notifyOnBookingCancelled);
        policy.setNotifyOnConsultantApprovalDecision(notifyOnConsultantApprovalDecision);
        policyRepository.saveNotificationPolicy(policy);
        return policy;
    }

    @Override
    public CancellationPolicy getCancellationPolicy() { return policyRepository.getCancellationPolicy(); }
    @Override
    public RefundPolicy getRefundPolicy() { return policyRepository.getRefundPolicy(); }
    @Override
    public PricingPolicy getPricingPolicy() { return policyRepository.getPricingPolicy(); }
    @Override
    public NotificationPolicy getNotificationPolicy() { return policyRepository.getNotificationPolicy(); }

    private void publishDecision(Consultant consultant, boolean approved) {
        if (policyRepository.getNotificationPolicy().isNotifyOnConsultantApprovalDecision()) {
            String message = approved ? "Consultant " + consultant.getName() + " was approved." : "Consultant " + consultant.getName() + " was rejected.";
            eventPublisher.publish(new ConsultantApprovalEvent(eventPublisher.nextEventId(), LocalDateTime.now(), message));
        }
    }
    
    private void ensureValidAdmin(String adminId) {
        if (adminId == null || adminId.isBlank())  throw new AuthorizationException("Admin ID is required.");
        if (!"admin-1".equals(adminId)) throw new AuthorizationException("Only an authorized admin can perform this action.");
    }
}
