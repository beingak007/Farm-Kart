package com.farmkart.farmer.repository;

import com.farmkart.farmer.client.enums.FarmerStatus;
import com.farmkart.farmer.repository.entity.Farmer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FarmerRepository extends JpaRepository<Farmer, Long> {

    Optional<Farmer> findByUserId(Long userId);

    boolean existsByUserId(Long userId);

    Page<Farmer> findByState(String state, Pageable pageable);

    Page<Farmer> findByStatus(FarmerStatus status, Pageable pageable);

    List<Farmer> findByStateAndDistrict(String state, String district);

    long countByStatus(FarmerStatus status);
}
