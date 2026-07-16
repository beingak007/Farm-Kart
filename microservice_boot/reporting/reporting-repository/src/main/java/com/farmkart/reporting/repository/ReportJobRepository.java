package com.farmkart.reporting.repository;

import com.farmkart.reporting.repository.entity.ReportJob;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportJobRepository extends JpaRepository<ReportJob, Long> {
    Page<ReportJob> findByReportType(String reportType, Pageable pageable);
    Page<ReportJob> findByStatus(String status, Pageable pageable);
    Page<ReportJob> findByRequestedBy(Long userId, Pageable pageable);
}
