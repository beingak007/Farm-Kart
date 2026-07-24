package com.farmkart.rest.controller;

import com.farmkart.starter.common.dto.ApiResponse;
import com.farmkart.client.dto.notification.SendNotificationRequest;
import com.farmkart.service.notification.NotificationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
@Tag(name = "Notification")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/send")
    public ApiResponse<Long> send(@Valid @RequestBody SendNotificationRequest request) {
        return ApiResponse.ok("Notification queued", notificationService.send(request));
    }
}
