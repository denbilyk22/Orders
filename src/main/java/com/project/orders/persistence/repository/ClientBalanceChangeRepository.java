package com.project.orders.persistence.repository;

import com.project.orders.persistence.model.ClientBalanceChange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.UUID;

@Repository
public interface ClientBalanceChangeRepository extends JpaRepository<ClientBalanceChange, UUID> {

    boolean existsByClientId(UUID clientId);

    @Query(value = """
           INSERT INTO client_balance_changes(client_id, amount, change_type)
           SELECT client_id, - SUM(amount), 'ADJUSTMENT'::client_balance_change_type
           FROM client_balance_changes
           GROUP BY client_id
           """, nativeQuery = true)
    @Modifying
    void refreshProfitForAllClients();

    @Query(value = """
           INSERT INTO client_balance_changes(client_id, amount, change_type)
           SELECT client_id, - SUM(amount), 'ADJUSTMENT'::client_balance_change_type
           FROM client_balance_changes
           WHERE client_id = :clientId
           GROUP BY client_id
           """, nativeQuery = true)
    @Modifying
    void refreshProfitForClient(UUID clientId);

    @Query(value = """
           SELECT COALESCE(SUM(amount), 0.0)
           FROM client_balance_changes cbc
           WHERE cbc.client_id = :clientId
           """, nativeQuery = true)
    BigDecimal getClientProfit(UUID clientId);
}
