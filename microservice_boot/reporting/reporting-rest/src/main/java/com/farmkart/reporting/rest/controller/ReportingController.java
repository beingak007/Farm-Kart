package com.farmkart.reporting.rest.controller;

import com.farmkart.reporting.client.dto.ReportRequest;
import com.farmkart.reporting.client.dto.ReportResponse;
import com.farmkart.reporting.service.ReportingService;
import com.farmkart.starter.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reports")
@Tag(name = "Reporting", description = "Operational and business report generation")
public class ReportingController {

    private final ReportingService reportingService;

    public ReportingController(ReportingService reportingService) {
        this.reportingService = reportingService;
    }

    @PostMapping
    @Operation(summary = "Submit a new report generation job")
    public ApiResponse<ReportResponse> submit(@RequestBody @Valid ReportRequest req,
                                               @RequestParam(defaultValue = "0") Long requestedBy) {
        return ApiResponse.ok(reportingService.submitReport(req, requestedBy));
    }

    @GetMapping("/{jobId}")
    @Operation(summary = "Check status of a report job")
    public ApiResponse<ReportResponse> getJob(@PathVariable Long jobId) {
        return ApiResponse.ok(reportingService.getJob(jobId));
    }

    @GetMapping("/type/{reportType}")
    @Operation(summary = "List all report jobs of a given type")
    public ApiResponse<Page<ReportResponse>> listByType(@PathVariable String reportType, Pageable pageable) {
        return ApiResponse.ok(reportingService.listByType(reportType, pageable));
    }
}
