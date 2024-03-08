package com.enigma.wmb_api.specification;

import com.enigma.wmb_api.dto.request.transaction.SearchTransactionRequest;
import com.enigma.wmb_api.entity.*;
import com.enigma.wmb_api.util.DateUtil;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TransactionSpecification {
    public static Specification<Bill> getSpecification(SearchTransactionRequest searchTransactionRequest) {
        return (root, query, criteriaBuilder) -> {

            // List Query Params
            List<Predicate> predicates = new ArrayList<>();

            // Search by User
            if (searchTransactionRequest.getUserName() != null) {
                Predicate userNamePredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("user").get("name")),
                        "%" + searchTransactionRequest.getUserName().toLowerCase() + "%"
                );
                predicates.add(userNamePredicate);
            }

            // Search by Name
            if (searchTransactionRequest.getMenuName() != null) {
                Predicate menuNamePredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("billDetails").get("menu").get("name")),
                        "%" + searchTransactionRequest.getMenuName().toLowerCase() + "%"
                );
                predicates.add(menuNamePredicate);
            }

            // Search by Trans Date
            if (searchTransactionRequest.getTransDate() != null) {
                Date tempDate = DateUtil.parseDate(searchTransactionRequest.getTransDate(), "yyyy-MM-dd");
                Predicate transDatePredicate = criteriaBuilder.equal(
                       criteriaBuilder.function("date", Date.class, root.get("transDate")), tempDate
                );
                predicates.add(transDatePredicate);
            }

            // Search by Start Trans Date
            if (searchTransactionRequest.getStartTransDate() != null) {
                Date tempDate = DateUtil.parseDate(searchTransactionRequest.getStartTransDate(), "yyyy-MM-dd");
                Predicate startTransDatePredicate = criteriaBuilder.greaterThanOrEqualTo(
                        criteriaBuilder.function("date", Date.class, root.get("transDate")), tempDate
                );
                predicates.add(startTransDatePredicate);
            }

            // Search by End Trans Date
            if (searchTransactionRequest.getEndTransDate() != null) {
                Date tempDate = DateUtil.parseDate(searchTransactionRequest.getEndTransDate(), "yyyy-MM-dd");
                Predicate endStartDatePredicate = criteriaBuilder.lessThanOrEqualTo(
                        criteriaBuilder.function("date", Date.class, root.get("transDate")), tempDate
                );

                predicates.add(endStartDatePredicate);

            }

            return query.where(predicates.toArray(new Predicate[]{})).getRestriction();
        };
    }
}
