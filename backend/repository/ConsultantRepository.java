package repository;

import java.util.List;
import java.util.Optional;

import model.user.Consultant;

public interface ConsultantRepository {
    Optional<Consultant> findById(String consultantId);
    List<Consultant> findAllApproved();
    List<Consultant> findPendingApproval();
    List<Consultant> findAll();
    void save(Consultant consultant);
}
