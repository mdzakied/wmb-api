package com.enigma.wmb_api.specification;

import com.enigma.wmb_api.dto.request.table.SearchTableRequest;
import com.enigma.wmb_api.entity.MTable;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;


public class TableSpecification {
    public static Specification<MTable> getSpecification(SearchTableRequest searchTableRequest) {
        return (root, query, criteriaBuilder) -> {
            // List Query Params
            List<Predicate> predicates = new ArrayList<>();

            // Search by Name
            if (searchTableRequest.getName() != null) {
                Predicate namePredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")),
                        "%" + searchTableRequest.getName().toLowerCase() + "%"
                );

                predicates.add(namePredicate);

            }

            return query.where(predicates.toArray(new Predicate[]{})).getRestriction();
        };
    }
}
