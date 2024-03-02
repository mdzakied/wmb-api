package com.enigma.wmb_api.specification;

import com.enigma.wmb_api.dto.request.menu.SearchMenuRequest;
import com.enigma.wmb_api.entity.Menu;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;


public class MenuSpecification {
    public static Specification<Menu> getSpecification(SearchMenuRequest searchMenuRequest) {
        return (root, query, criteriaBuilder) -> {
            // List Query Params
            List<Predicate> predicates = new ArrayList<>();

            // Search by Name
            if (searchMenuRequest.getName() != null) {
                Predicate namePredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")),
                        "%" + searchMenuRequest.getName().toLowerCase() + "%"
                );

                predicates.add(namePredicate);

            }

            return query.where(predicates.toArray(new Predicate[]{})).getRestriction();
        };
    }
}
