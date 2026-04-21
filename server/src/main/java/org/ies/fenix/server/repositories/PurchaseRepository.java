package org.ies.fenix.server.repositories;

import org.ies.fenix.server.models.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Integer> {
    List<Purchase> findByClientId(Integer clientId);
    boolean existsByClientIdAndGameId(Integer clientId, Integer gameId);
}