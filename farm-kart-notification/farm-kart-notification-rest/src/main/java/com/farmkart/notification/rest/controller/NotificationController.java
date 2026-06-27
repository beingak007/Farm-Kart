package com.farmkart.notification.rest.controller;

import com.farmkart.notification.client.dto.NotificationResponse;
import com.farmkart.notification.client.dto.SendNotificationRequest;
import com.farmkart.notification.client.enums.NotificationStatus;
import com.farmkart.notification.service.NotificationService;
import com.farmkart.starter.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
@Tag(name = "Notification", description = "Send and track SMS / Email / Push notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/send")
    @Operation(summary = "Send a notification immediately")
    public ApiResponse<NotificationResponse> send(@RequestBody @Valid SendNotificationRequest req) {
        return ApiResponse.ok(notificationService.send(req));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get notification history for a user")
    public ApiResponse<Page<NotificationResponse>> getByUser(@PathVariable Long userId, Pageable pageable) {
        return ApiResponse.ok(notificationService.getByUser(userId, pageable));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "List notifications by delivery status")
    public ApiResponse<Page<NotificationResponse>> getByStatus(@PathVariable NotificationStatus status,
                                                                Pageable pageable) {
        return ApiResponse.ok(notificationService.getByStatus(status, pageable));
    }
}
