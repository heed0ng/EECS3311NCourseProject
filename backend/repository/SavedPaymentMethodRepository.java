package repository;

import java.util.List;
import java.util.Optional;

import model.payment.SavedPaymentMethod;

public interface SavedPaymentMethodRepository {
    Optional<SavedPaymentMethod> findById(String savedMethodId);
    List<SavedPaymentMethod> findByClient(String clientId);
    void save(SavedPaymentMethod savedPaymentMethod);
    void delete(String savedMethodId);
}
