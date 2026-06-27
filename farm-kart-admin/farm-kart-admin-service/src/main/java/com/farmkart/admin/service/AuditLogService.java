package com.farmkart.admin.service;

import com.farmkart.admin.client.dto.AuditLogResponse;
import com.farmkart.admin.client.dto.CreateAuditLogRequest;
import com.farmkart.admin.repository.AuditLogRepository;
import com.farmkart.admin.repository.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditLogService {

    private final AuditLogRepository auditRepo;

    public AuditLogService(AuditLogRepository auditRepo) {
        this.auditRepo = auditRepo;
    }

    @Transactional
    public AuditLogResponse create(CreateAuditLogRequest req) {
        AuditLog log = new AuditLog();
        log.setUserId(req.userId());
        log.setServiceName(req.serviceName());
        log.setAction(req.action());
        log.setResourceType(req.resourceType());
        log.setResourceId(req.resourceId());
        log.setIpAddress(req.ipAddress());
        log.setDescription(req.description());
        if (req.outcome() != null) log.setOutcome(req.outcome());
        return toResponse(auditRepo.save(log));
    }

    @Transactional(readOnly = true)
    public Page<AuditLogResponse> getByUser(Long userId, Pageable pageable) {
        return auditRepo.findByUserId(userId, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<AuditLogResponse> getByService(String serviceName, Pageable pageable) {
        return auditRepo.findByServiceName(serviceName, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<AuditLogResponse> getAll(Pageable pageable) {
        return auditRepo.findAll(pageable).map(this::toResponse);
    }

    private AuditLogResponse toResponse(AuditLog a) {
        return new AuditLogResponse(a.getId(), a.getUserId(), a.getServiceName(), a.getAction(),
                a.getResourceType(), a.getResourceId(), a.getIpAddress(),
                a.getDescription(), a.getOutcome(), a.getCreatedAt());
    }
}
