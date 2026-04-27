package com.restaurant.backend.payment.controller;

import com.restaurant.backend.common.response.ApiResponse;
import com.restaurant.backend.payment.dto.PaymentSummaryDto;
import com.restaurant.backend.payment.service.PaymentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/sample")
    public ApiResponse<PaymentSummaryDto> getSamplePayment() {
        return ApiResponse.success(paymentService.getSamplePayment());
    }
}
