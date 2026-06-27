package com.farmkart.reporting.client.dto;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public record ReportResponse(
        Long id,
        String reportType,
        String status,          // PENDING | PROCESSING | COMPLETED | FAILED
        String fromDate,
        String toDate,
        Long totalRecords,
        List<Map<String, Object>> data,
        String downloadUrl,
        Instant generatedAt
) {}
