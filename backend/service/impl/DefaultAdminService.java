package backend.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import backend.model.notification.ConsultantApprovalEvent;
import backend.model.policy.*;
import backend.model.user.Admin;
import backend.model.user.Consultant;
import backend.observer.EventPublisher;
import backend.repository.AdminRepository;
import backend.repository.ConsultantRepository;
import backend.repository.PolicyRepository;
import backend.service.AdminService;
import backend.util.AuthorizationException;
import backend.util.ConsultantApprovalStatus;
import backend.util.EntityNotFoundException;

public class DefaultAdminService implements AdminService {
    private final AdminRepository adminRepository;
    private final ConsultantRepository consultantRepository;
    private final PolicyRepository policyRepository;
    private final EventPublisher eventPublisher;

    public DefaultAdminService(AdminRepository adminRepository, ConsultantRepository consultantRepository, PolicyRepository policyRepository, EventPublisher eventPublisher) {
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

        Consultant consultant = this.consultantRepository.findById(consultantId)
                .orElseThrow(() -> new EntityNotFoundException("Consultant not found."));

        consultant.setApprovalStatus(ConsultantApprovalStatus.APPROVED);
        this.consultantRepository.save(consultant);
        this.publishDecision(consultant, true);
        return consultant;
    }

    @Override
    public Consultant rejectConsultant(String adminId, String consultantId) {
        this.requireExistingAdmin(adminId);

        Consultant consultant = consultantRepository.findById(consultantId)
                .orElseThrow(() -> new EntityNotFoundException("Consultant not found."));

        consultant.setApprovalStatus(ConsultantApprovalStatus.REJECTED);
        this.consultantRepository.save(consultant);
        this.publishDecision(consultant, false);
        return consultant;
    }

    @Override
    public CancellationPolicy updateCancellationPolicy(String adminId, int cancellationDeadlineHours) {
        this.requireExistingAdmin(adminId);

        CancellationPolicy policy = this.policyRepository.getCancellationPolicy();
        policy.setCancellationDeadlineHours(cancellationDeadlineHours);
        this.policyRepository.saveCancellationPolicy(policy);
        return policy;
    }

    @Override
    public RefundPolicy updateRefundPolicy(String adminId, double refundPercentBeforeDeadline, double refundPercentAfterDeadline) {
        this.requireExistingAdmin(adminId);

        RefundPolicy policy = this.policyRepository.getRefundPolicy();
        policy.setRefundPercentBeforeDeadline(refundPercentBeforeDeadline);
        policy.setRefundPercentAfterDeadline(refundPercentAfterDeadline);
        this.policyRepository.saveRefundPolicy(policy);
        return policy;
    }

    @Override // Allowing Custom pricing set by consultant rather than using base price
    public CustomPricingPolicy updatePricingPolicy(String adminId, boolean allowConsultantCustomPrice) {
        this.requireExistingAdmin(adminId);

        CustomPricingPolicy policy = this.policyRepository.getPricingPolicy();
        policy.setAllowConsultantCustomPrice(allowConsultantCustomPrice);
        this.policyRepository.savePricingPolicy(policy);
        return policy;
    }

    @Override
    public NotificationPolicy updateNotificationPolicy(String adminId, boolean notifyOnBookingRequested, boolean notifyOnBookingAccepted, boolean notifyOnBookingRejected,
            boolean notifyOnPaymentProcessed, boolean notifyOnBookingCancelled, boolean notifyOnConsultantApprovalDecision) {
        this.requireExistingAdmin(adminId);

        NotificationPolicy policy = this.policyRepository.getNotificationPolicy();
        policy.setNotifyOnBookingRequested(notifyOnBookingRequested);
        policy.setNotifyOnBookingAccepted(notifyOnBookingAccepted);
        policy.setNotifyOnBookingRejected(notifyOnBookingRejected);
        policy.setNotifyOnPaymentProcessed(notifyOnPaymentProcessed);
        policy.setNotifyOnBookingCancelled(notifyOnBookingCancelled);
        policy.setNotifyOnConsultantApprovalDecision(notifyOnConsultantApprovalDecision);
        this.policyRepository.saveNotificationPolicy(policy);
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
        return adminRepository.findById(adminId).orElseThrow(() -> new AuthorizationException("Only a persisted admin can perform this action."));
    }

    private void publishDecision(Consultant consultant, boolean approved) {
        if (policyRepository.getNotificationPolicy().isNotifyOnConsultantApprovalDecision()) {
            String message = approved
                    ? "Consultant " + consultant.getName() + " was approved."
                    : "Consultant " + consultant.getName() + " was rejected.";

            eventPublisher.publish(new ConsultantApprovalEvent(eventPublisher.nextEventId(), LocalDateTime.now(), message));
        }
    }
}