package repository;

import model.policy.CancellationPolicy;
import model.policy.NotificationPolicy;
import model.policy.PricingPolicy;
import model.policy.RefundPolicy;

public interface PolicyRepository {
    CancellationPolicy getCancellationPolicy();
    RefundPolicy getRefundPolicy();
    PricingPolicy getPricingPolicy();
    NotificationPolicy getNotificationPolicy();
    void saveCancellationPolicy(CancellationPolicy policy);
    void saveRefundPolicy(RefundPolicy policy);
    void savePricingPolicy(PricingPolicy policy);
    void saveNotificationPolicy(NotificationPolicy policy);
}
