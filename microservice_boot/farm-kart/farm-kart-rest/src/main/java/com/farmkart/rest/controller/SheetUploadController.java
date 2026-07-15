package com.farmkart.rest.controller;

import com.farmkart.client.dto.sheet.SheetUploadResponse;
import com.farmkart.service.sheet.SheetUploadService;
import com.farmkart.starter.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/sheets")
@Tag(name = "Sheet Upload")
public class SheetUploadController {

    private final SheetUploadService sheetUploadService;

    public SheetUploadController(SheetUploadService sheetUploadService) {
        this.sheetUploadService = sheetUploadService;
    }

    @PostMapping("/upload")
    @Operation(summary = "Upload a product sheet (CSV/XLS/XLSX) — stores to S3 and queues Kafka processing")
    public ApiResponse<SheetUploadResponse> upload(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam("file") MultipartFile file) throws IOException {
        return ApiResponse.ok("Upload accepted", sheetUploadService.upload(userId, file));
    }

    @GetMapping
    @Operation(summary = "List all sheet uploads for the current user")
    public ApiResponse<List<SheetUploadResponse>> list(@RequestHeader("X-User-Id") Long userId) {
        return ApiResponse.ok(sheetUploadService.listByUser(userId));
    }

    @GetMapping("/{uploadId}/status")
    @Operation(summary = "Poll the processing status of a specific upload (QUEUED → PROCESSING → PROCESSED | FAILED)")
    public ApiResponse<SheetUploadResponse> status(@PathVariable Long uploadId) {
        return ApiResponse.ok(sheetUploadService.getStatus(uploadId));
    }
}
