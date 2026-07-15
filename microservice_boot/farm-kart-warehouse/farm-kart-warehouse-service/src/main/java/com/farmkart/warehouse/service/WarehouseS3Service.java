package com.farmkart.warehouse.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.time.Duration;

@Service
public class WarehouseS3Service {

    private final S3Client s3Client;
    private final S3Presigner presigner;
    private final String bucket;
    private final Duration presignExpiry;

    public WarehouseS3Service(
            @Value("${farmkart.s3.region:ap-south-1}") String region,
            @Value("${farmkart.s3.bucket:farmkart-dev-uploads}") String bucket,
            @Value("${farmkart.s3.presign-expiry-seconds:604800}") long presignExpirySeconds) {
        Region awsRegion = Region.of(region);
        this.bucket = bucket;
        this.presignExpiry = Duration.ofSeconds(presignExpirySeconds);
        this.s3Client = S3Client.builder().region(awsRegion).build();
        this.presigner = S3Presigner.builder().region(awsRegion).build();
    }

    public String presignedUrl(String key) {
        if (key == null || key.isBlank()) {
            return null;
        }
        GetObjectPresignRequest request = GetObjectPresignRequest.builder()
                .signatureDuration(presignExpiry)
                .getObjectRequest(builder -> builder.bucket(bucket).key(key))
                .build();
        return presigner.presignGetObject(request).url().toString();
    }

    public void upload(String key, byte[] data, String contentType) {
        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .contentType(contentType)
                        .build(),
                RequestBody.fromBytes(data));
    }
}
