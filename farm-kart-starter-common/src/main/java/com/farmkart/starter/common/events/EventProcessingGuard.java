package com.farmkart.starter.common.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * Idempotent event processing guard.
 * In-memory deduplication suitable for single-instance dev.
 * Production deployments should replace with a durable store (see admin/notification services).
 */
public class EventProcessingGuard {

    private static final Logger log = LoggerFactory.getLogger(EventProcessingGuard.class);

    private final Set<String> processed = ConcurrentHashMap.newKeySet();

    /**
     * Runs {@code action} only once per {@code eventId}.
     *
     * @return true if action ran, false if duplicate
     */
    public boolean runOnce(String eventId, Runnable action) {
        if (eventId == null || eventId.isBlank()) {
            action.run();
            return true;
        }
        if (!processed.add(eventId)) {
            log.debug("Skipping duplicate event eventId={}", eventId);
            return false;
        }
        action.run();
        return true;
    }

    public <T> T runOnce(String eventId, Supplier<T> action) {
        if (eventId == null || eventId.isBlank()) {
            return action.get();
        }
        if (!processed.add(eventId)) {
            log.debug("Skipping duplicate event eventId={}", eventId);
            return null;
        }
        return action.get();
    }
}
