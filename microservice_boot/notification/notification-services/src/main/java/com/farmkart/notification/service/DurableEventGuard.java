package com.farmkart.notification.service;

import com.farmkart.notification.repository.ProcessedDomainEventRepository;
import com.farmkart.notification.repository.entity.ProcessedDomainEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DurableEventGuard {

    private static final Logger log = LoggerFactory.getLogger(DurableEventGuard.class);

    private final ProcessedDomainEventRepository processedRepo;

    public DurableEventGuard(ProcessedDomainEventRepository processedRepo) {
        this.processedRepo = processedRepo;
    }

    @Transactional
    public boolean runOnce(String eventId, String topic, Runnable action) {
        if (eventId == null || eventId.isBlank()) {
            action.run();
            return true;
        }
        if (processedRepo.existsById(eventId)) {
            log.debug("Skipping duplicate event eventId={} topic={}", eventId, topic);
            return false;
        }
        action.run();
        try {
            processedRepo.save(new ProcessedDomainEvent(eventId, topic));
            return true;
        } catch (DataIntegrityViolationException ex) {
            log.debug("Concurrent duplicate event eventId={} topic={}", eventId, topic);
            return false;
        }
    }
}
