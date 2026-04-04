package backend.repository;

import java.util.List;
import java.util.Optional;

import backend.model.core.AvailabilitySlot;

public interface AvailabilitySlotRepository {
    Optional<AvailabilitySlot> findById(String slotId);
    List<AvailabilitySlot> findAvailableByConsultant(String consultantId);
    List<AvailabilitySlot> findByConsultant(String consultantId);
    List<AvailabilitySlot> findAllAvailable();
    void save(AvailabilitySlot slot);
}
