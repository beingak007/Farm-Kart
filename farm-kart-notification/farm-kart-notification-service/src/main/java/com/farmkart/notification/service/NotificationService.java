package com.farmkart.notification.service;

import com.farmkart.notification.client.dto.NotificationResponse;
import com.farmkart.notification.client.dto.SendNotificationRequest;
import com.farmkart.notification.client.enums.NotificationChannel;
import com.farmkart.notification.client.enums.NotificationStatus;
import com.farmkart.notification.repository.NotificationLogRepository;
import com.farmkart.notification.repository.entity.NotificationLog;
import com.farmkart.starter.common.events.FkTopics;
import com.farmkart.starter.common.events.NotificationTriggeredEvent;
import com.farmkart.starter.common.sms.SmsGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationLogRepository notifRepo;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final SmsGateway smsGateway;

    public NotificationService(NotificationLogRepository notifRepo,
                                KafkaTemplate<String, Object> kafkaTemplate,
                                SmsGateway smsGateway) {
        this.notifRepo = notifRepo;
        this.kafkaTemplate = kafkaTemplate;
        this.smsGateway = smsGateway;
    }

    /**
     * Domain-event-driven notification (Kafka consumer path).
     */
    @Transactional
    public void sendDomainNotification(Long userId, NotificationChannel channel, String contact,
                                       String templateCode, Map<String, String> templateVars) {
        log.info("Domain notification userId={} template={}", userId, templateCode);
        NotificationLog entry = buildLog(userId, channel, contact, templateCode,
                renderMessage(templateCode, templateVars, null));
        dispatch(entry);
        notifRepo.save(entry);
    }

    /**
     * Synchronous send (REST-triggered).
     */
    @Transactional
    public NotificationResponse send(SendNotificationRequest req) {
        NotificationLog entry = buildLog(req.userId(), req.channel(), req.recipientContact(),
                req.templateCode(), renderMessage(req.templateCode(), req.templateVars(), req.body()));
        dispatch(entry);
        return toResponse(notifRepo.save(entry));
    }

    /**
     * Kafka-triggered send — listens on notification.triggered topic.
     */
    @KafkaListener(topics = FkTopics.NOTIFICATION_TRIGGERED, groupId = "notification-service")
    public void onNotificationTriggered(NotificationTriggeredEvent event) {
        log.info("Notification triggered: userId={} channel={} template={}",
                event.userId(), event.channel(), event.templateCode());
        NotificationLog entry = buildLog(
                event.userId(),
                NotificationChannel.valueOf(event.channel()),
                event.recipientContact(),
                event.templateCode(),
                renderMessage(event.templateCode(), event.templateVars(), null));
        dispatch(entry);
        notifRepo.save(entry);
    }

    @Transactional(readOnly = true)
    public Page<NotificationResponse> getByUser(Long userId, Pageable pageable) {
        return notifRepo.findByUserId(userId, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<NotificationResponse> getByStatus(NotificationStatus status, Pageable pageable) {
        return notifRepo.findByStatus(status, pageable).map(this::toResponse);
    }

    // ── Private helpers ───────────────────────────────────────────────────

    private NotificationLog buildLog(Long userId, NotificationChannel channel,
                                      String contact, String templateCode, String rendered) {
        NotificationLog entry = new NotificationLog();
        entry.setUserId(userId);
        entry.setChannel(channel);
        entry.setRecipientContact(contact);
        entry.setTemplateCode(templateCode);
        entry.setRenderedMessage(rendered);
        return entry;
    }

    /**
     * Dispatch to the appropriate channel gateway.
     * Replace stubs with real SMS/email/push SDK calls in production.
     */
    private void dispatch(NotificationLog entry) {
        try {
            switch (entry.getChannel()) {
                case SMS      -> sendSms(entry.getRecipientContact(), entry.getRenderedMessage());
                case EMAIL    -> sendEmail(entry.getRecipientContact(), entry.getRenderedMessage());
                case PUSH     -> sendPush(entry.getUserId(), entry.getRenderedMessage());
                case WHATSAPP -> sendWhatsApp(entry.getRecipientContact(), entry.getRenderedMessage());
            }
            entry.setStatus(NotificationStatus.SENT);
            entry.setSentAt(Instant.now());
        } catch (Exception ex) {
            log.error("Notification dispatch failed: {}", ex.getMessage());
            entry.setStatus(NotificationStatus.FAILED);
            entry.setFailureReason(ex.getMessage());
        }
    }

    private String renderMessage(String templateCode, Map<String, String> vars, String fallbackBody) {
        if (fallbackBody != null && !fallbackBody.isBlank()) return fallbackBody;
        if ("OTP_LOGIN".equals(templateCode) && vars != null && vars.containsKey("otp")) {
            return "Your Farm Kart OTP is " + vars.get("otp") + ". Valid for 5 minutes. Do not share.";
        }
        if ("WELCOME".equals(templateCode)) {
            return "Welcome to Farm Kart! Your account is ready.";
        }
        if ("FARMER_WELCOME".equals(templateCode) && vars != null) {
            return "Welcome to Farm Kart! Farm " + vars.getOrDefault("farmName", "") + " registered in "
                    + vars.getOrDefault("state", "") + ".";
        }
        if ("FARMER_VERIFIED".equals(templateCode)) {
            return "Your Farm Kart farmer profile has been verified. You can now list crops.";
        }
        if ("ORDER_CONFIRMED".equals(templateCode) && vars != null) {
            return "Order #" + vars.getOrDefault("orderId", "") + " confirmed. Total: INR "
                    + vars.getOrDefault("total", "") + ".";
        }
        if ("PAYMENT_RECEIPT".equals(templateCode) && vars != null) {
            return "Payment received for order #" + vars.getOrDefault("orderId", "") + ". Amount: INR "
                    + vars.getOrDefault("amount", "") + ".";
        }
        if ("ORDER_DELIVERED".equals(templateCode) && vars != null) {
            return "Order #" + vars.getOrDefault("orderId", "") + " delivered. Tracking: "
                    + vars.getOrDefault("trackingNumber", "") + ".";
        }
        if (vars == null || vars.isEmpty()) return "[" + templateCode + "]";
        StringBuilder sb = new StringBuilder("[" + templateCode + "] ");
        vars.forEach((k, v) -> sb.append(k).append("=").append(v).append(" "));
        return sb.toString();
    }

    private void sendSms(String phone, String message) {
        if (message != null && message.contains("OTP is")) {
            String otp = extractOtp(message);
            if (otp != null) {
                smsGateway.sendOtp(phone, otp);
                return;
            }
        }
        smsGateway.send(phone, message);
    }

    private String extractOtp(String message) {
        int start = message.indexOf("OTP is ");
        if (start < 0) return null;
        start += "OTP is ".length();
        int end = message.indexOf('.', start);
        if (end < 0) return null;
        return message.substring(start, end).trim();
    }

    private void sendEmail(String email, String message) {
        log.info("EMAIL → {} : {}", email, message);
    }

    private void sendPush(Long userId, String message) {
        log.info("PUSH → userId={} : {}", userId, message);
    }

    private void sendWhatsApp(String phone, String message) {
        log.info("WHATSAPP → {} : {}", phone, message);
    }

    private NotificationResponse toResponse(NotificationLog n) {
        return new NotificationResponse(n.getId(), n.getUserId(), n.getChannel(),
                n.getRecipientContact(), n.getTemplateCode(), n.getRenderedMessage(),
                n.getStatus(), n.getFailureReason(), n.getSentAt(), n.getCreatedAt());
    }
}
