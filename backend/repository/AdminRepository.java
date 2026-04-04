package backend.repository;

import java.util.List;
import java.util.Optional;

import backend.model.user.Admin;

public interface AdminRepository {
    Optional<Admin> findById(String adminId);
    List<Admin> findAll();
    void save(Admin admin);
}