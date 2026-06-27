package com.farmkart.admin.rest.controller;

import com.farmkart.admin.client.dto.AuditLogResponse;
import com.farmkart.admin.client.dto.CreateAuditLogRequest;
import com.farmkart.admin.service.AuditLogService;
import com.farmkart.starter.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/audit-logs")
@Tag(name = "Admin", description = "Platform audit logs and monitoring")
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @PostMapping
    public ApiResponse<AuditLogResponse> create(@RequestBody @Valid CreateAuditLogRequest req) {
        return ApiResponse.ok(auditLogService.create(req));
    }

    @GetMapping
    public ApiResponse<Page<AuditLogResponse>> getAll(Pageable pageable) {
        return ApiResponse.ok(auditLogService.getAll(pageable));
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<Page<AuditLogResponse>> getByUser(@PathVariable Long userId, Pageable pageable) {
        return ApiResponse.ok(auditLogService.getByUser(userId, pageable));
    }

    @GetMapping("/service/{serviceName}")
    public ApiResponse<Page<AuditLogResponse>> getByService(@PathVariable String serviceName, Pageable pageable) {
        return ApiResponse.ok(auditLogService.getByService(serviceName, pageable));
    }
}
