package com.farmkart.notification.service;

import com.farmkart.notification.client.dto.NotificationResponse;
import com.farmkart.notification.client.dto.SendNotificationRequest;
import com.farmkart.notification.client.enums.NotificationChannel;
import com.farmkart.notification.client.enums.NotificationStatus;
import com.farmkart.notification.client.enums.NotificationTemplateCodeEnum;
import com.farmkart.notification.client.enums.NotificationTemplateVarEnum;
import com.farmkart.notification.constants.NotificationServiceConstants;
import com.farmkart.notification.repository.NotificationLogRepository;
import com.farmkart.notification.repository.entity.NotificationLog;
import com.farmkart.starter.common.enums.FkCurrencyEnum;
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

    @Transactional
    public void sendDomainNotification(Long userId, NotificationChannel channel, String contact,
                                       NotificationTemplateCodeEnum templateCode,
                                       Map<String, String> templateVars) {
        log.info("Domain notification userId={} template={}", userId, templateCode);
        NotificationLog entry = buildLog(userId, channel, contact, templateCode.getValue(),
                renderMessage(templateCode, templateVars, null));
        dispatch(entry);
        notifRepo.save(entry);
    }

    @Transactional
    public NotificationResponse send(SendNotificationRequest req) {
        NotificationTemplateCodeEnum template = NotificationTemplateCodeEnum
                .getNotificationTemplateCodeEnum(req.templateCode());
        NotificationLog entry = buildLog(req.userId(), req.channel(), req.recipientContact(),
                req.templateCode(), renderMessage(template, req.templateVars(), req.body()));
        dispatch(entry);
        return toResponse(notifRepo.save(entry));
    }

    @KafkaListener(topics = FkTopics.NOTIFICATION_TRIGGERED, groupId = NotificationServiceConstants.KAFKA_GROUP_DISPATCH)
    public void onNotificationTriggered(NotificationTriggeredEvent event) {
        log.info("Notification triggered: userId={} channel={} template={}",
                event.userId(), event.channel(), event.templateCode());
        NotificationChannel channel = NotificationChannel.getNotificationChannel(event.channel());
        NotificationTemplateCodeEnum template = NotificationTemplateCodeEnum
                .getNotificationTemplateCodeEnum(event.templateCode());
        NotificationLog entry = buildLog(
                event.userId(),
                channel != null ? channel : NotificationChannel.SMS,
                event.recipientContact(),
                event.templateCode(),
                renderMessage(template, event.templateVars(), null));
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

    private String renderMessage(NotificationTemplateCodeEnum templateCode, Map<String, String> vars,
                                 String fallbackBody) {
        if (fallbackBody != null && !fallbackBody.isBlank()) {
            return fallbackBody;
        }
        if (templateCode == null) {
            return fallbackBody;
        }
        return switch (templateCode) {
            case OTP_LOGIN -> vars != null && vars.containsKey(NotificationTemplateVarEnum.OTP.getValue())
                    ? "Your Farm Kart OTP is " + vars.get(NotificationTemplateVarEnum.OTP.getValue())
                    + ". Valid for 5 minutes. Do not share."
                    : "[" + templateCode.getValue() + "]";
            case WELCOME -> "Welcome to Farm Kart! Your account is ready.";
            case FARMER_WELCOME -> "Welcome to Farm Kart! Farm "
                    + varOrEmpty(vars, NotificationTemplateVarEnum.FARM_NAME) + " registered in "
                    + varOrEmpty(vars, NotificationTemplateVarEnum.STATE) + ".";
            case FARMER_VERIFIED -> "Your Farm Kart farmer profile has been verified. You can now list crops.";
            case ORDER_CONFIRMED -> "Order #" + varOrEmpty(vars, NotificationTemplateVarEnum.ORDER_ID)
                    + " confirmed. Total: " + formatMoney(vars, NotificationTemplateVarEnum.TOTAL) + ".";
            case PAYMENT_RECEIPT -> "Payment received for order #"
                    + varOrEmpty(vars, NotificationTemplateVarEnum.ORDER_ID) + ". Amount: "
                    + formatMoney(vars, NotificationTemplateVarEnum.AMOUNT) + ".";
            case ORDER_DELIVERED -> "Order #" + varOrEmpty(vars, NotificationTemplateVarEnum.ORDER_ID)
                    + " delivered. Tracking: "
                    + varOrEmpty(vars, NotificationTemplateVarEnum.TRACKING_NUMBER) + ".";
            case WAREHOUSE_BOOKED -> "Warehouse confirmed! "
                    + varOrEmpty(vars, NotificationTemplateVarEnum.WAREHOUSE_NAME)
                    + " (" + varOrEmpty(vars, NotificationTemplateVarEnum.DISTANCE_KM) + " km away). Rent "
                    + formatMoney(vars, NotificationTemplateVarEnum.RENT)
                    + " for " + varOrEmpty(vars, NotificationTemplateVarEnum.QUANTITY_TONS) + "t ("
                    + varOrEmpty(vars, NotificationTemplateVarEnum.START_DATE) + " to "
                    + varOrEmpty(vars, NotificationTemplateVarEnum.END_DATE) + "). Booking #"
                    + varOrEmpty(vars, NotificationTemplateVarEnum.BOOKING_ID) + ".";
            case WAREHOUSE_LIVE_LOCATION -> "Live location: "
                    + varOrEmpty(vars, NotificationTemplateVarEnum.WAREHOUSE_NAME)
                    + " — open map: " + varOrEmpty(vars, NotificationTemplateVarEnum.MAPS_URL)
                    + " (Booking #" + varOrEmpty(vars, NotificationTemplateVarEnum.BOOKING_ID) + ").";
        };
    }

    private String varOrEmpty(Map<String, String> vars, NotificationTemplateVarEnum key) {
        if (vars == null) {
            return "";
        }
        return vars.getOrDefault(key.getValue(), "");
    }

    private String formatMoney(Map<String, String> vars, NotificationTemplateVarEnum amountKey) {
        String amount = varOrEmpty(vars, amountKey);
        String currencyCode = varOrEmpty(vars, NotificationTemplateVarEnum.CURRENCY);
        FkCurrencyEnum currency = FkCurrencyEnum.resolve(currencyCode.isBlank() ? null : currencyCode);
        return currency.formatAmount(amount);
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
