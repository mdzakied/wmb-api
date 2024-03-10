package com.enigma.wmb_api.repositry;

import com.enigma.wmb_api.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface PaymentRepository extends JpaRepository<Payment, String> {
}
