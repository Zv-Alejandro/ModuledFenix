package org.ies.fenix.server.repositories;

import org.ies.fenix.server.models.AuthToken;
import org.ies.fenix.server.models.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Integer> {
    Optional<Client> findByUsername(String username);
    Optional<Client> findByEmail(String email);
    Client findByAuthTokensToken(String token);
}