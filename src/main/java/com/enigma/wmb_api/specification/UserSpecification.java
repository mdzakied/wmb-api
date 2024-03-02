package com.enigma.wmb_api.specification;

import com.enigma.wmb_api.dto.request.user.SearchUserRequest;
import com.enigma.wmb_api.entity.User;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class UserSpecification {
    public static Specification<User> getSpecification(SearchUserRequest searchUserRequest){
        return (root, query, criteriaBuilder) -> {
            // List Query Params
            List<Predicate> predicates = new ArrayList<>();

            // Search by Name
            if (searchUserRequest.getName() != null){
                Predicate namePredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")),
                        "%" + searchUserRequest.getName().toLowerCase() + "%"
                );

                predicates.add(namePredicate);

            }

            // Search by Phone Number
            if (searchUserRequest.getPhoneNumber() != null) {
                Predicate phoneNumberPredicate = criteriaBuilder.like(
                        root.get("phoneNumber"),
                        "%" + searchUserRequest.getPhoneNumber() + "%"
                );

                predicates.add(phoneNumberPredicate);
            }

            return query.where(predicates.toArray(new Predicate[]{})).getRestriction();
        };
    }
}
