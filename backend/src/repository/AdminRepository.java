package repository;

import java.util.List;
import java.util.Optional;

import model.user.Admin;

public interface AdminRepository {
    Optional<Admin> findById(String adminId);
    List<Admin> findAll();
    void save(Admin admin);
}