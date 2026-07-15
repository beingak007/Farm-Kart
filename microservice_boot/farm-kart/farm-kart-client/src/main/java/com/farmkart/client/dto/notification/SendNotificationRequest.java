package com.farmkart.client.dto.notification;

import com.farmkart.client.enums.NotificationChannel;

import java.util.Map;

public record SendNotificationRequest(
        Long userId,
        String type,
        NotificationChannel channel,
        Map<String, Object> payload
) {
}
