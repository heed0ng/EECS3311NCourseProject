package backend.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import backend.model.notification.ConsultantApprovalEvent;
import backend.model.notification.PolicyUpdatedEvent;
import backend.model.policy.*;
import backend.model.user.Admin;
import backend.model.user.Consultant;
import backend.observer.EventPublisher;
import backend.repository.AdminRepository;
import backend.repository.ConsultantRepository;
import backend.repository.PolicyRepository;
import backend.service.AdminService;
import backend.util.AuthorizationException;
import backend.util.BusinessRuleViolationException;
import backend.util.ConsultantApprovalStatus;
import backend.util.EntityNotFoundException;

public class DefaultAdminService implements AdminService {
    private final AdminRepository adminRepository;
    private final ConsultantRepository consultantRepository;
    private final PolicyRepository policyRepository;
    private final EventPublisher eventPublisher;

    public DefaultAdminService(AdminRepository adminRepository, ConsultantRepository consultantRepository,
            PolicyRepository policyRepository, EventPublisher eventPublisher) {
        this.adminRepository = adminRepository;
        this.consultantRepository = consultantRepository;
        this.policyRepository = policyRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public List<Consultant> getPendingConsultants() {
        return this.consultantRepository.findPendingApproval();
    }

    @Override
    public Consultant approveConsultant(String adminId, String consultantId) {
        this.requireExistingAdmin(adminId);

        Consultant consultant = this.consultantRepository.findById(consultantId).orElseThrow(() -> new EntityNotFoundException("Consultant not found."));

        consultant.setApprovalStatus(ConsultantApprovalStatus.APPROVED);
        this.consultantRepository.save(consultant);
        this.publishDecision(consultant, true);
        return consultant;
    }

    @Override
    public Consultant rejectConsultant(String adminId, String consultantId) {
        this.requireExistingAdmin(adminId);

        Consultant consultant = this.consultantRepository.findById(consultantId).orElseThrow(() -> new EntityNotFoundException("Consultant not found."));

        consultant.setApprovalStatus(ConsultantApprovalStatus.REJECTED);
        this.consultantRepository.save(consultant);
        this.publishDecision(consultant, false);
        return consultant;
    }

    @Override
    public CancellationPolicy updateCancellationPolicy(String adminId, int cancellationDeadlineHours) {
        Admin admin = this.requireExistingAdmin(adminId);

        if (cancellationDeadlineHours < 0) throw new BusinessRuleViolationException("Cancellation deadline hours cannot be negative.");

        CancellationPolicy policy = this.policyRepository.getCancellationPolicy();
        policy.setCancellationDeadlineHours(cancellationDeadlineHours);
        this.policyRepository.saveCancellationPolicy(policy);

        this.publishPolicyUpdate(admin.getName() + " updated the cancellation policy. New cancellation deadline: " 
        + cancellationDeadlineHours + " hour(s).");
        return policy;
    }

    @Override
    public RefundPolicy updateRefundPolicy(String adminId, double refundPercentBeforeDeadline, double refundPercentAfterDeadline) {

        Admin admin = this.requireExistingAdmin(adminId);

        if (refundPercentBeforeDeadline < 0.0 || refundPercentBeforeDeadline > 100.0) throw new BusinessRuleViolationException("Refund percentage for eligible cancellations must be between 0 and 100.");

        RefundPolicy policy = this.policyRepository.getRefundPolicy();
        policy.setRefundPercentBeforeDeadline(refundPercentBeforeDeadline);
        policy.setRefundPercentAfterDeadline(0.0);
        this.policyRepository.saveRefundPolicy(policy);
        this.publishPolicyUpdate(admin.getName() + " updated the refund policy. Eligible cancellations now refund " 
        + refundPercentBeforeDeadline + "%.");

        return policy;
    }

    @Override
    public CustomPricingPolicy updatePricingPolicy(String adminId, boolean allowConsultantCustomPrice) {
        Admin admin = this.requireExistingAdmin(adminId);

        CustomPricingPolicy policy = this.policyRepository.getPricingPolicy();
        policy.setAllowConsultantCustomPrice(allowConsultantCustomPrice);
        this.policyRepository.savePricingPolicy(policy);

        this.publishPolicyUpdate(admin.getName() + " updated the pricing policy. Consultant custom price is now "
                        + (allowConsultantCustomPrice ? "ENABLED." : "DISABLED."));

        return policy;
    }

    @Override
    public NotificationPolicy updateNotificationPolicy(String adminId, boolean notifyOnBookingRequested, 
    		boolean notifyOnBookingAccepted, boolean notifyOnBookingRejected, boolean notifyOnPaymentProcessed, 
            boolean notifyOnBookingCancelled, boolean notifyOnConsultantApprovalDecision) {

        Admin admin = this.requireExistingAdmin(adminId);

        NotificationPolicy policy = this.policyRepository.getNotificationPolicy();
        policy.setNotifyOnBookingRequested(notifyOnBookingRequested);
        policy.setNotifyOnBookingAccepted(notifyOnBookingAccepted);
        policy.setNotifyOnBookingRejected(notifyOnBookingRejected);
        policy.setNotifyOnPaymentProcessed(notifyOnPaymentProcessed);
        policy.setNotifyOnBookingCancelled(notifyOnBookingCancelled);
        policy.setNotifyOnConsultantApprovalDecision(notifyOnConsultantApprovalDecision);
        this.policyRepository.saveNotificationPolicy(policy);

        this.publishPolicyUpdate(admin.getName() + " updated the notification policy settings.");

        return policy;
    }

    @Override
    public CancellationPolicy getCancellationPolicy() {
        return this.policyRepository.getCancellationPolicy();
    }

    @Override
    public RefundPolicy getRefundPolicy() {
        return this.policyRepository.getRefundPolicy();
    }

    @Override
    public CustomPricingPolicy getPricingPolicy() {
        return this.policyRepository.getPricingPolicy();
    }

    @Override
    public NotificationPolicy getNotificationPolicy() {
        return this.policyRepository.getNotificationPolicy();
    }

    private Admin requireExistingAdmin(String adminId) {
        if (adminId == null || adminId.isBlank()) throw new AuthorizationException("Admin ID is required.");

        return this.adminRepository.findById(adminId).orElseThrow(() -> new AuthorizationException("Only a persisted admin can perform this action."));
    }

    private void publishDecision(Consultant consultant, boolean approved) {
        if (this.policyRepository.getNotificationPolicy().isNotifyOnConsultantApprovalDecision()) {
            String message = approved ? "Consultant " + consultant.getName() + " was approved." : "Consultant " + consultant.getName() + " was rejected.";

            this.eventPublisher.publish(new ConsultantApprovalEvent(this.eventPublisher.nextEventId(), LocalDateTime.now(), message));
        }
    }

    private void publishPolicyUpdate(String message) {
        this.eventPublisher.publish(new PolicyUpdatedEvent(this.eventPublisher.nextEventId(), LocalDateTime.now(), message));
    }
}