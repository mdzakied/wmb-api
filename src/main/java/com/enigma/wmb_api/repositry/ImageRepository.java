package com.enigma.wmb_api.repositry;

import com.enigma.wmb_api.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface ImageRepository extends JpaRepository<Image, String>, JpaSpecificationExecutor<Image> {

}
