package com.farmkart.service.kafka.event;

import java.time.Instant;

public record SheetUploadedEvent(
        Long uploadId,
        Long userId,
        String s3Key,
        String fileType,
        long fileSize,
        String originalName,
        Instant uploadedAt
) {
}
