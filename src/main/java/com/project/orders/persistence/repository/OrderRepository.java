package com.project.orders.persistence.repository;

import com.project.orders.persistence.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID>, JpaSpecificationExecutor<Order> {

    @Query(value = """
           SELECT EXISTS(
               SELECT 1
               FROM orders o
               WHERE o.name = :name
                 AND o.supplier_id = :supplierId
                 AND o.consumer_id = :consumerId
           ) 
           """, nativeQuery = true)
    boolean isSimilarOrderExist(String name, UUID supplierId, UUID consumerId);

}
