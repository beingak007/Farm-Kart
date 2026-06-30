package com.farmkart.service.kafka;

import com.farmkart.starter.common.events.DomainEventPublisher;
import com.farmkart.starter.common.events.FkBaseEvent;
import com.farmkart.starter.common.events.FkTopics;
import com.farmkart.starter.common.events.SheetUploadedDomainEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SheetUploadProducer {

    private final DomainEventPublisher eventPublisher;
    private final String topic;

    public SheetUploadProducer(
            DomainEventPublisher eventPublisher,
            @Value("${farmkart.kafka.topics.sheet-uploaded:farmkart.sheet.uploaded}") String topic) {
        this.eventPublisher = eventPublisher;
        this.topic = topic;
    }

    public void publish(SheetUploadedDomainEvent event) {
        eventPublisher.publish(topic, String.valueOf(event.uploadId()), event);
    }

    public SheetUploadedDomainEvent wrap(Payload payload) {
        return new SheetUploadedDomainEvent(
                new FkBaseEvent(FkTopics.SHEET_UPLOADED, "marketplace-service"),
                payload.uploadId(),
                payload.userId(),
                payload.s3Key(),
                payload.fileType(),
                payload.fileSize(),
                payload.originalName(),
                payload.uploadedAt());
    }

    /** Backward-compatible payload builder for upload service. */
    public record Payload(
            Long uploadId,
            Long userId,
            String s3Key,
            String fileType,
            long fileSize,
            String originalName,
            java.time.Instant uploadedAt
    ) {}
}
