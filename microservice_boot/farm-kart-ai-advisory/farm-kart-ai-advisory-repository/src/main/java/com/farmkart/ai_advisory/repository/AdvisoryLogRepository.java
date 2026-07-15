package com.farmkart.ai_advisory.repository;

import com.farmkart.ai_advisory.repository.entity.AdvisoryLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdvisoryLogRepository extends JpaRepository<AdvisoryLog, Long> {
    Page<AdvisoryLog> findByFarmerId(Long farmerId, Pageable pageable);
    Page<AdvisoryLog> findByAdvisoryType(String advisoryType, Pageable pageable);
}
