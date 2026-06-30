package com.farmkart.service.sheet;

import com.farmkart.client.dto.sheet.SheetUploadResponse;
import com.farmkart.repository.SheetUploadRepository;
import com.farmkart.repository.entity.SheetUpload;
import com.farmkart.service.kafka.SheetUploadProducer;
import com.farmkart.service.storage.S3Service;
import com.farmkart.starter.common.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class SheetUploadService {

    private static final Logger log = LoggerFactory.getLogger(SheetUploadService.class);
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("csv", "xlsx", "xls");

    private final SheetUploadRepository sheetUploadRepository;
    private final S3Service s3Service;
    private final SheetUploadProducer producer;

    public SheetUploadService(
            SheetUploadRepository sheetUploadRepository,
            S3Service s3Service,
            SheetUploadProducer producer) {
        this.sheetUploadRepository = sheetUploadRepository;
        this.s3Service = s3Service;
        this.producer = producer;
    }

    /**
     * Upload a sheet to S3, persist metadata, then fire a Kafka event for async processing.
     * Status lifecycle: QUEUED → PROCESSING → PROCESSED | FAILED
     */
    @Transactional
    public SheetUploadResponse upload(Long userId, MultipartFile file) throws IOException {
        validateFile(file);

        String extension = getExtension(file.getOriginalFilename());
        String s3Key = "sheets/" + userId + "/" + UUID.randomUUID() + "." + extension;
        String contentType = resolveContentType(extension);

        // Upload to S3
        byte[] bytes = file.getBytes();
        s3Service.upload(s3Key, bytes, contentType);
        String presignedUrl = s3Service.presignedUrl(s3Key);

        // Persist
        SheetUpload upload = new SheetUpload();
        upload.setUserId(userId);
        upload.setFileName(s3Key.substring(s3Key.lastIndexOf('/') + 1));
        upload.setOriginalName(file.getOriginalFilename());
        upload.setFileType(extension);
        upload.setFileSize(file.getSize());
        upload.setS3Key(s3Key);
        upload.setS3Url(presignedUrl);
        upload.setStatus("QUEUED");
        sheetUploadRepository.save(upload);

        // Fire Kafka event for async row-counting / import
        producer.publish(producer.wrap(new SheetUploadProducer.Payload(
                upload.getId(),
                userId,
                s3Key,
                extension,
                file.getSize(),
                file.getOriginalFilename(),
                Instant.now())));

        log.info("[Sheet] Uploaded to S3 and queued: uploadId={} s3Key={}", upload.getId(), s3Key);

        return toResponse(upload, "Sheet uploaded — processing in background");
    }

    public List<SheetUploadResponse> listByUser(Long userId) {
        return sheetUploadRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(u -> toResponse(u, null))
                .toList();
    }

    public SheetUploadResponse getStatus(Long uploadId) {
        SheetUpload upload = sheetUploadRepository.findById(uploadId)
                .orElseThrow(() -> new BusinessException(404, "Upload not found"));
        return toResponse(upload, null);
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("Please select a file to upload");
        }
        String name = file.getOriginalFilename();
        if (name == null || name.isBlank()) {
            throw new BusinessException("Invalid file name");
        }
        if (!ALLOWED_EXTENSIONS.contains(getExtension(name))) {
            throw new BusinessException("Only CSV, XLS, and XLSX files are allowed");
        }
    }

    private String getExtension(String filename) {
        int dot = filename.lastIndexOf('.');
        return dot >= 0 ? filename.substring(dot + 1).toLowerCase() : "";
    }

    private String resolveContentType(String extension) {
        return switch (extension) {
            case "csv" -> "text/csv";
            case "xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "xls" -> "application/vnd.ms-excel";
            default -> "application/octet-stream";
        };
    }

    private SheetUploadResponse toResponse(SheetUpload u, String message) {
        return new SheetUploadResponse(
                u.getId(),
                u.getOriginalName(),
                u.getFileType(),
                u.getFileSize(),
                u.getRowCount(),
                u.getStatus(),
                u.getS3Url(),
                u.getErrorMessage(),
                message);
    }
}
