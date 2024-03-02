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

            // Search by Price
            if (searchMenuRequest.getPrice() != null) {
                Predicate pricePredicate = criteriaBuilder.equal(
                        root.get("price"),
                        searchMenuRequest.getPrice()
                );

                predicates.add(pricePredicate);

            }

            // Search by Min Price
            if (searchMenuRequest.getMinPrice() != null) {
                Predicate minPricePredicate = criteriaBuilder.greaterThanOrEqualTo(
                        root.get("price"),
                        searchMenuRequest.getMinPrice()
                );

                predicates.add(minPricePredicate);

            }

            // Search by Max Price
            if (searchMenuRequest.getMaxPrice() != null) {
                Predicate maxPricePredicate = criteriaBuilder.lessThanOrEqualTo(
                        root.get("price"),
                        searchMenuRequest.getMaxPrice()
                );

                predicates.add(maxPricePredicate);

            }

            return query.where(predicates.toArray(new Predicate[]{})).getRestriction();
        };
    }
}
