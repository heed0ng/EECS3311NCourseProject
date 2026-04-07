package backend.repository;

import java.util.List;
import java.util.Optional;

import backend.model.user.Consultant;

public interface ConsultantRepository {
    Optional<Consultant> findById(String consultantId);
    List<Consultant> findAllApproved();
    List<Consultant> findPendingApproval();
    List<Consultant> findAll();
    void save(Consultant consultant);
}
