package com.farmkart.client.dto.sheet;

public record SheetUploadResponse(
        Long id,
        String originalName,
        String fileType,
        long fileSize,
        Integer rowCount,
        String status,
        String s3Url,
        String errorMessage,
        String message
) {}
