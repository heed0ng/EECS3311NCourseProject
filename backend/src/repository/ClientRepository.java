package backend.repository;

import java.util.List;
import java.util.Optional;

import backend.model.user.Client;

public interface ClientRepository {
    Optional<Client> findById(String clientId);
    List<Client> findAll();
    void save(Client client);
}
