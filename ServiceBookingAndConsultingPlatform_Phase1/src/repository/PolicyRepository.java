package repository;

import model.policy.CancellationPolicy;
import model.policy.NotificationPolicy;
import model.policy.CustomPricingPolicy;
import model.policy.RefundPolicy;

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
