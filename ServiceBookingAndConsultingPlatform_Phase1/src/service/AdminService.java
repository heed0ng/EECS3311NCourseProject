package service;

import java.util.List;

import model.policy.*;
import model.user.Consultant;

public interface AdminService {
    List<Consultant> getPendingConsultants();
    Consultant approveConsultant(String adminId, String consultantId);
    Consultant rejectConsultant(String adminId, String consultantId);
    CancellationPolicy updateCancellationPolicy(String adminId, int cancellationDeadlineHours);
    RefundPolicy updateRefundPolicy(String adminId, double refundPercentBeforeDeadline, double refundPercentAfterDeadline);
    PricingPolicy updatePricingPolicy(String adminId, boolean allowConsultantCustomPrice);
    
    NotificationPolicy updateNotificationPolicy(
    		String adminId, boolean notifyOnBookingRequested,
            boolean notifyOnBookingAccepted, boolean notifyOnBookingRejected,
            boolean notifyOnPaymentProcessed, boolean notifyOnBookingCancelled,
            boolean notifyOnConsultantApprovalDecision);
    
    CancellationPolicy getCancellationPolicy();
    RefundPolicy getRefundPolicy();
    PricingPolicy getPricingPolicy();
    NotificationPolicy getNotificationPolicy();
}
