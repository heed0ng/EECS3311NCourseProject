package repository;

import java.util.List;
import java.util.Optional;

import model.user.Client;

public interface ClientRepository {
    Optional<Client> findById(String clientId);
    List<Client> findAll();
    void save(Client client);
}
