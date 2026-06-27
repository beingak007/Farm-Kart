package com.farmkart.service.notification;

import com.farmkart.client.dto.notification.SendNotificationRequest;
import com.farmkart.repository.entity.Notification;
import com.farmkart.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Transactional
    public Long send(SendNotificationRequest request) {
        Notification notification = new Notification();
        notification.setUserId(request.userId());
        notification.setType(request.type());
        notification.setChannel(request.channel());
        notification.setPayload(request.payload() != null ? request.payload() : java.util.Map.of());
        notification.setSentAt(Instant.now());
        notificationRepository.save(notification);

        log.info("Notification sent: userId={}, type={}, channel={}",
                request.userId(), request.type(), request.channel());
        return notification.getId();
    }
}
