package com.farmkart.service.payment;

import com.farmkart.client.dto.payment.PaymentResponse;
import com.farmkart.client.dto.payment.PaymentWebhookRequest;
import com.farmkart.repository.entity.Payment;
import com.farmkart.client.enums.PaymentRecordStatus;
import com.farmkart.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
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
        payment.setStatus(mapStatus(request.status()));
        paymentRepository.save(payment);
        return toResponse(payment);
    }

    private PaymentRecordStatus mapStatus(String status) {
        return switch (status.toUpperCase()) {
            case "SUCCESS", "CAPTURED" -> PaymentRecordStatus.SUCCESS;
            case "FAILED" -> PaymentRecordStatus.FAILED;
            case "REFUNDED" -> PaymentRecordStatus.REFUNDED;
            default -> PaymentRecordStatus.INITIATED;
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
}
