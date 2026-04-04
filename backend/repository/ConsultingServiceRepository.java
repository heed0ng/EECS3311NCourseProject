package backend.repository;

import java.util.List;
import java.util.Optional;

import backend.model.core.ConsultingService;

public interface ConsultingServiceRepository {
    Optional<ConsultingService> findById(String serviceId);
    List<ConsultingService> findAllActive();
    List<ConsultingService> findAll();
    void save(ConsultingService consultingService);
}
