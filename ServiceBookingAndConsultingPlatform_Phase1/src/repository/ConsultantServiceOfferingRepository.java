package repository;

import java.util.List;
import java.util.Optional;

import model.core.ConsultantServiceOffering;

public interface ConsultantServiceOfferingRepository {
    Optional<ConsultantServiceOffering> findById(String offeringId);
    List<ConsultantServiceOffering> findAllActive();
    List<ConsultantServiceOffering> findByConsultant(String consultantId);
    void save(ConsultantServiceOffering offering);
}
