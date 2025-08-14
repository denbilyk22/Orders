package com.project.orders.persistence.specification;

import com.project.orders.persistence.model.Order;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class OrderSpecification {

    private static final String ID = "id";
    private static final String SUPPLIER = "supplier";
    private static final String CONSUMER = "consumer";
    private static final String CREATED_DATE = "createdDate";

    public static Specification<Order> get(UUID supplierId, UUID consumerId) {
        return (root, query, criteriaBuilder) ->
                getPredicate(supplierId, consumerId, root, query, criteriaBuilder);
    }

    private static Predicate getPredicate(UUID supplierId, UUID consumerId, Root<Order> root, CriteriaQuery<?> query,
                                          CriteriaBuilder criteriaBuilder) {
        var predicates = new ArrayList<Predicate>();

        if (Objects.nonNull(supplierId)) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(SUPPLIER).get(ID), supplierId));
        }

        if (Objects.nonNull(consumerId)) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(CONSUMER).get(ID), supplierId));
        }

        query.orderBy(criteriaBuilder.desc(root.get(CREATED_DATE)));
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

}
