package com.enigma.wmb_api.repositry;

import com.enigma.wmb_api.entity.TransType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TransTypeRepository extends JpaRepository<TransType, String>, JpaSpecificationExecutor<TransType> {
}
