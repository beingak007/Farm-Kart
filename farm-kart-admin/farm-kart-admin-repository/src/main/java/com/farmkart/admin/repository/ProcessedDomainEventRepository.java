package com.farmkart.admin.repository;

import com.farmkart.admin.repository.entity.ProcessedDomainEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedDomainEventRepository extends JpaRepository<ProcessedDomainEvent, String> {
}
