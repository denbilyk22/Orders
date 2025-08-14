package com.project.orders.persistence.specification;

import com.project.orders.persistence.model.Client;
import jakarta.persistence.criteria.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ClientSpecification {

    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String EMAIL = "email";
    private static final String ADDRESS = "address";
    private static final String BALANCE_CHANGES = "balanceChanges";
    private static final String AMOUNT = "amount";
    private static final String CREATED_DATE = "createdDate";
    private static final String CLIENT = "client";

    public static Specification<Client> get(String search, BigDecimal profitFrom, BigDecimal profitTo) {
        return (root, query, criteriaBuilder) ->
                getPredicate(search, profitFrom, profitTo, root, query, criteriaBuilder);
    }

    private static Predicate getPredicate(String search, BigDecimal profitFrom, BigDecimal profitTo, Root<Client> root,
                                          CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        var predicates = new ArrayList<Predicate>();

        if (StringUtils.isNotBlank(search)) {
            predicates.add(getPredicateForSearch(search, root, criteriaBuilder));
        }

        if (Objects.nonNull(profitFrom) || Objects.nonNull(profitTo)) {
            addHavingClauseForProfitRange(profitFrom, profitTo, root, query, criteriaBuilder);
        }

        query.distinct(true);
        query.orderBy(criteriaBuilder.desc(root.get(CREATED_DATE)));
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    private static Predicate getPredicateForSearch(String search, Root<Client> root, CriteriaBuilder cb) {
        var predicates = new ArrayList<Predicate>();

        var searchStrings = new HashSet<String>();
        searchStrings.add(search);

        searchStrings.addAll(splitStringOnPartsWithExactLength(search, 3));

        searchStrings.forEach(searchPart -> {
            var likeExp = "%%%s%%".formatted(searchPart)
                    .toLowerCase();

            predicates.add(cb.like(cb.lower(root.get(NAME)), likeExp));
            predicates.add(cb.like(cb.lower(root.get(EMAIL)), likeExp));
            predicates.add(cb.like(cb.lower(root.get(ADDRESS)), likeExp));
        });

        return cb.or(predicates.toArray(new Predicate[0]));
    }

    private static void addHavingClauseForProfitRange(BigDecimal profitFrom, BigDecimal profitTo, Root<Client> root,
                                                      CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        var predicates = new ArrayList<Predicate>();

        var balanceJoin = root.join(BALANCE_CHANGES, JoinType.LEFT);
        var sumExpression = criteriaBuilder.coalesce(criteriaBuilder.sum(balanceJoin.get(AMOUNT)), BigDecimal.ZERO);

        if (Objects.nonNull(profitFrom)) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(sumExpression, profitFrom));
        }

        if (Objects.nonNull(profitTo)) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(sumExpression, profitTo));
        }

        query.groupBy(root);
        query.having(criteriaBuilder.and(predicates.toArray(new Predicate[0])));
    }

    private static List<String> splitStringOnPartsWithExactLength(String string, int length) {
        if (string.length() <= length) {
            return List.of(string);
        }

        var parts = new ArrayList<String>();

        for (int i = 0; i + length <= string.length(); i++) {
            parts.add(string.substring(i, i + length));
        }

        return parts;
    }
}
