package com.farmkart.starter.common.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * Publishes domain events to Kafka after the database transaction commits.
 * Prevents ghost events when a transaction rolls back.
 */
public class DomainEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(DomainEventPublisher.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public DomainEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(String topic, String partitionKey, Object payload) {
        Runnable send = () -> {
            kafkaTemplate.send(topic, partitionKey, payload)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("Failed to publish event topic={} key={}", topic, partitionKey, ex);
                        } else {
                            log.debug("Published event topic={} key={} offset={}",
                                    topic, partitionKey,
                                    result != null ? result.getRecordMetadata().offset() : null);
                        }
                    });
        };

        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    send.run();
                }
            });
        } else {
            send.run();
        }
    }
}
