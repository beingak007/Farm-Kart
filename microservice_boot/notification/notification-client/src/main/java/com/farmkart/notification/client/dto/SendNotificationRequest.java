package com.farmkart.notification.client.dto;

import com.farmkart.notification.client.enums.NotificationChannel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

public record SendNotificationRequest(
        @NotNull Long userId,
        @NotNull NotificationChannel channel,
        @NotBlank String recipientContact,   // phone or email
        @NotBlank String templateCode,
        Map<String, String> templateVars,
        String title,                        // for PUSH
        String body
) {}
