package com.farmkart.notification.repository;

import com.farmkart.notification.repository.entity.ProcessedDomainEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedDomainEventRepository extends JpaRepository<ProcessedDomainEvent, String> {
}
