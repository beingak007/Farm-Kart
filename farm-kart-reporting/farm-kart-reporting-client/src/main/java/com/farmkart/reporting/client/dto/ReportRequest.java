package com.farmkart.reporting.client.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.Map;

public record ReportRequest(
        @NotBlank String reportType,   // SALES | FARMER_ACTIVITY | BUYER_ACTIVITY | ORDER_SUMMARY | PAYMENT_SUMMARY
        @NotNull LocalDate fromDate,
        @NotNull LocalDate toDate,
        Map<String, String> filters,   // e.g. state=Maharashtra, cropName=Wheat
        String format                  // JSON | CSV (default JSON)
) {}
