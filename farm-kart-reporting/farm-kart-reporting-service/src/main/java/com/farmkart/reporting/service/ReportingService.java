package com.farmkart.reporting.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.farmkart.reporting.client.dto.ReportRequest;
import com.farmkart.reporting.client.dto.ReportResponse;
import com.farmkart.reporting.repository.ReportJobRepository;
import com.farmkart.reporting.repository.entity.ReportJob;
import com.farmkart.starter.common.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
public class ReportingService {

    private static final Logger log = LoggerFactory.getLogger(ReportingService.class);

    private final ReportJobRepository reportRepo;
    private final ObjectMapper objectMapper;

    public ReportingService(ReportJobRepository reportRepo, ObjectMapper objectMapper) {
        this.reportRepo = reportRepo;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public ReportResponse submitReport(ReportRequest req, Long requestedByUserId) {
        ReportJob job = new ReportJob();
        job.setReportType(req.reportType());
        job.setFromDate(req.fromDate());
        job.setToDate(req.toDate());
        job.setFormat(req.format() != null ? req.format() : "JSON");
        job.setRequestedBy(requestedByUserId);
        try {
            job.setFiltersJson(objectMapper.writeValueAsString(req.filters()));
        } catch (JsonProcessingException e) {
            throw new BusinessException(400, "Invalid filters");
        }
        job = reportRepo.save(job);
        generateAsync(job.getId());
        return toResponse(job, List.of());
    }

    @Transactional(readOnly = true)
    public ReportResponse getJob(Long jobId) {
        return toResponse(reportRepo.findById(jobId)
                .orElseThrow(() -> new BusinessException(404, "Report job not found: " + jobId)), List.of());
    }

    @Transactional(readOnly = true)
    public Page<ReportResponse> listByType(String reportType, Pageable pageable) {
        return reportRepo.findByReportType(reportType, pageable)
                .map(j -> toResponse(j, List.of()));
    }

    @Async
    protected void generateAsync(Long jobId) {
        try {
            Thread.sleep(500); // simulate processing delay
        } catch (InterruptedException ignored) {}

        ReportJob job = reportRepo.findById(jobId).orElse(null);
        if (job == null) return;
        try {
            job.setStatus("PROCESSING");
            reportRepo.save(job);

            // Stub: real implementation queries the relevant service DB / data warehouse
            long records = switch (job.getReportType()) {
                case "SALES"           -> 1250L;
                case "FARMER_ACTIVITY" -> 380L;
                case "BUYER_ACTIVITY"  -> 290L;
                case "ORDER_SUMMARY"   -> 540L;
                case "PAYMENT_SUMMARY" -> 540L;
                default                -> 0L;
            };

            job.setStatus("COMPLETED");
            job.setTotalRecords(records);
            job.setCompletedAt(Instant.now());
        } catch (Exception ex) {
            log.error("Report generation failed: {}", ex.getMessage());
            job.setStatus("FAILED");
            job.setErrorMessage(ex.getMessage());
        }
        reportRepo.save(job);
    }

    private ReportResponse toResponse(ReportJob j, List<Map<String, Object>> data) {
        return new ReportResponse(j.getId(), j.getReportType(), j.getStatus(),
                j.getFromDate() != null ? j.getFromDate().toString() : null,
                j.getToDate()   != null ? j.getToDate().toString()   : null,
                j.getTotalRecords(), data, j.getResultUrl(), j.getCompletedAt());
    }
}
