package com.farmkart.notification.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "processed_domain_events")
public class ProcessedDomainEvent {

    @Id
    @Column(name = "event_id", length = 36, nullable = false)
    private String eventId;

    @Column(name = "topic", nullable = false)
    private String topic;

    @CreationTimestamp
    @Column(name = "processed_at", nullable = false, updatable = false)
    private Instant processedAt;

    protected ProcessedDomainEvent() {}

    public ProcessedDomainEvent(String eventId, String topic) {
        this.eventId = eventId;
        this.topic = topic;
    }

    public String getEventId() { return eventId; }
    public String getTopic() { return topic; }
    public Instant getProcessedAt() { return processedAt; }
}
