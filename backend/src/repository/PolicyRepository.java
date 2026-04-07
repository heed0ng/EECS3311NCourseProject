package backend.repository;

import backend.model.policy.CancellationPolicy;
import backend.model.policy.NotificationPolicy;
import backend.model.policy.CustomPricingPolicy;
import backend.model.policy.RefundPolicy;

public interface PolicyRepository {
    CancellationPolicy getCancellationPolicy();
    RefundPolicy getRefundPolicy();
    CustomPricingPolicy getPricingPolicy();
    NotificationPolicy getNotificationPolicy();
    void saveCancellationPolicy(CancellationPolicy policy);
    void saveRefundPolicy(RefundPolicy policy);
    void savePricingPolicy(CustomPricingPolicy policy);
    void saveNotificationPolicy(NotificationPolicy policy);
}
