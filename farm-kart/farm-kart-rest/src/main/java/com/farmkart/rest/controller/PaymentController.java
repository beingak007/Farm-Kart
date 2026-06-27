package com.farmkart.rest.controller;

import com.farmkart.starter.common.dto.ApiResponse;
import com.farmkart.client.dto.payment.PaymentResponse;
import com.farmkart.client.dto.payment.PaymentWebhookRequest;
import com.farmkart.service.payment.PaymentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@Tag(name = "Payment")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/webhook")
    public ApiResponse<PaymentResponse> webhook(@RequestBody PaymentWebhookRequest request) {
        return ApiResponse.ok("Webhook processed", paymentService.handleWebhook(request));
    }
}
