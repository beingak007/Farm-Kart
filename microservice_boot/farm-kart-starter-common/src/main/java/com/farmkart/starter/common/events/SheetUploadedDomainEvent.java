package com.farmkart.starter.common.events;

import java.time.Instant;

public record SheetUploadedDomainEvent(
        FkBaseEvent base,
        Long uploadId,
        Long userId,
        String s3Key,
        String fileType,
        long fileSize,
        String originalName,
        Instant uploadedAt
) {}
