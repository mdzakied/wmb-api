package com.enigma.wmb_api.repositry;

import com.enigma.wmb_api.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TransactionRepository extends JpaRepository<Bill, String>, JpaSpecificationExecutor<Bill> {
}
