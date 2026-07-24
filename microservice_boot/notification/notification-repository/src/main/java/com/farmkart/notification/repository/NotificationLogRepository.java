package com.farmkart.notification.repository;

import com.farmkart.notification.client.enums.NotificationChannel;
import com.farmkart.notification.client.enums.NotificationStatus;
import com.farmkart.notification.repository.entity.NotificationLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationLogRepository extends JpaRepository<NotificationLog, Long> {
    Page<NotificationLog> findByUserId(Long userId, Pageable pageable);
    Page<NotificationLog> findByStatus(NotificationStatus status, Pageable pageable);
    Page<NotificationLog> findByChannel(NotificationChannel channel, Pageable pageable);
    long countByStatus(NotificationStatus status);
}
