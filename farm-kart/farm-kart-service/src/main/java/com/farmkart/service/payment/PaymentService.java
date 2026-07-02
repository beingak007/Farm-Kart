package com.farmkart.service.payment;

import com.farmkart.client.constants.MarketplaceServiceConstants;
import com.farmkart.client.dto.payment.PaymentResponse;
import com.farmkart.client.dto.payment.PaymentWebhookRequest;
import com.farmkart.client.enums.PaymentGatewayStatusEnum;
import com.farmkart.client.enums.PaymentRecordStatus;
import com.farmkart.repository.PaymentRepository;
import com.farmkart.repository.entity.Payment;
import com.farmkart.starter.common.constants.FkCurrencyConstants;
import com.farmkart.starter.common.enums.FkCurrencyEnum;
import com.farmkart.starter.common.events.DomainEventPublisher;
import com.farmkart.starter.common.events.FkBaseEvent;
import com.farmkart.starter.common.events.FkTopics;
import com.farmkart.starter.common.events.PaymentFailedEvent;
import com.farmkart.starter.common.events.PaymentSuccessEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final DomainEventPublisher eventPublisher;

    public PaymentService(PaymentRepository paymentRepository, DomainEventPublisher eventPublisher) {
        this.paymentRepository = paymentRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public PaymentResponse handleWebhook(PaymentWebhookRequest request) {
        Payment payment = paymentRepository.findByProviderPaymentId(request.providerPaymentId())
                .orElseGet(() -> {
                    Payment p = new Payment();
                    p.setOrderId(request.orderId());
                    p.setPaymentProvider("razorpay");
                    p.setProviderPaymentId(request.providerPaymentId());
                    p.setAmount(java.math.BigDecimal.ZERO);
                    return p;
                });

        if (request.metadata() != null) {
            payment.setMetadata(request.metadata());
        }
        PaymentRecordStatus previousStatus = payment.getStatus();
        payment.setStatus(mapStatus(request.status()));
        paymentRepository.save(payment);
        publishPaymentEvent(payment, previousStatus, request.status());
        return toResponse(payment);
    }

    private void publishPaymentEvent(Payment payment, PaymentRecordStatus previousStatus, String rawStatus) {
        if (payment.getStatus() == previousStatus) {
            return;
        }
        if (payment.getStatus() == PaymentRecordStatus.SUCCESS) {
            String currency = resolveCurrency(payment.getMetadata());
            PaymentSuccessEvent event = new PaymentSuccessEvent(
                    new FkBaseEvent(FkTopics.PAYMENT_SUCCESS, MarketplaceServiceConstants.SERVICE_NAME),
                    payment.getId(), payment.getOrderId(), null,
                    payment.getAmount(), currency, payment.getProviderPaymentId(), Instant.now());
            eventPublisher.publish(FkTopics.PAYMENT_SUCCESS, String.valueOf(payment.getId()), event);
        } else if (payment.getStatus() == PaymentRecordStatus.FAILED) {
            String currency = resolveCurrency(payment.getMetadata());
            PaymentFailedEvent event = new PaymentFailedEvent(
                    new FkBaseEvent(FkTopics.PAYMENT_FAILED, MarketplaceServiceConstants.SERVICE_NAME),
                    payment.getId(), payment.getOrderId(), payment.getAmount(),
                    currency, rawStatus, Instant.now());
            eventPublisher.publish(FkTopics.PAYMENT_FAILED, String.valueOf(payment.getId()), event);
        }
    }

    private PaymentRecordStatus mapStatus(String status) {
        PaymentGatewayStatusEnum gatewayStatus = PaymentGatewayStatusEnum.getPaymentGatewayStatusEnum(
                status != null ? status.toUpperCase() : null);
        if (gatewayStatus == null) {
            return PaymentRecordStatus.INITIATED;
        }
        return switch (gatewayStatus) {
            case SUCCESS, CAPTURED -> PaymentRecordStatus.SUCCESS;
            case FAILED -> PaymentRecordStatus.FAILED;
            case REFUNDED -> PaymentRecordStatus.REFUNDED;
        };
    }

    private PaymentResponse toResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getOrderId(),
                payment.getPaymentProvider(),
                payment.getProviderPaymentId(),
                payment.getAmount(),
                payment.getStatus());
    }

    private String resolveCurrency(java.util.Map<String, Object> metadata) {
        if (metadata == null || !metadata.containsKey("currency")) {
            return FkCurrencyConstants.DEFAULT.getValue();
        }
        return FkCurrencyEnum.resolve(String.valueOf(metadata.get("currency"))).getValue();
    }
}
