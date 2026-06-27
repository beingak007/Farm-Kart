package com.farmkart.notification.client.dto;

import com.farmkart.notification.client.enums.NotificationChannel;
import com.farmkart.notification.client.enums.NotificationStatus;

import java.time.Instant;

public record NotificationResponse(
        Long id,
        Long userId,
        NotificationChannel channel,
        String recipientContact,
        String templateCode,
        String renderedMessage,
        NotificationStatus status,
        String failureReason,
        Instant sentAt,
        Instant createdAt
) {}
