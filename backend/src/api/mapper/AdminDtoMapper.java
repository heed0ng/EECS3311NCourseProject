package backend.api.mapper;

import backend.api.dto.response.PolicySummaryResponse;
import backend.model.policy.CancellationPolicy;
import backend.model.policy.CustomPricingPolicy;
import backend.model.policy.NotificationPolicy;
import backend.model.policy.RefundPolicy;

public final class AdminDtoMapper {

    private AdminDtoMapper() {
    }

    public static PolicySummaryResponse toPolicySummaryResponse(CancellationPolicy cancellationPolicy, CustomPricingPolicy pricingPolicy,
        NotificationPolicy notificationPolicy, RefundPolicy refundPolicy) {

    String cancellationSummary = "Cancellation deadline hours: " + cancellationPolicy.getCancellationDeadlineHours();

    String pricingSummary = "Allow consultant custom price: " + pricingPolicy.isAllowConsultantCustomPrice();

    String notificationSummary = "notifyOnBookingRequested=" + notificationPolicy.isNotifyOnBookingRequested()
            + ", notifyOnBookingAccepted=" + notificationPolicy.isNotifyOnBookingAccepted()
            + ", notifyOnBookingRejected=" + notificationPolicy.isNotifyOnBookingRejected()
            + ", notifyOnPaymentProcessed=" + notificationPolicy.isNotifyOnPaymentProcessed()
            + ", notifyOnBookingCancelled=" + notificationPolicy.isNotifyOnBookingCancelled()
            + ", notifyOnConsultantApprovalDecision=" + notificationPolicy.isNotifyOnConsultantApprovalDecision();

    String refundSummary = "Refund rate for eligible cancellations: " + refundPolicy.getRefundPercentBeforeDeadline() + "%";

    return new PolicySummaryResponse(cancellationSummary, pricingSummary, notificationSummary, refundSummary);
	}
}