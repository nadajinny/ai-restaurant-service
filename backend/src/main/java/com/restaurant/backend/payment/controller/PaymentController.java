package com.restaurant.backend.payment.controller;

import com.restaurant.backend.common.response.ApiResponse;
import com.restaurant.backend.payment.dto.PaymentCreateRequest;
import com.restaurant.backend.payment.dto.PaymentResponse;
import com.restaurant.backend.payment.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ApiResponse<PaymentResponse> createPayment(@Valid @RequestBody PaymentCreateRequest request) {
        return ApiResponse.success("결제가 처리되었습니다.", paymentService.createPayment(request));
    }

    @GetMapping("/{paymentId}")
    public ApiResponse<PaymentResponse> getPayment(@PathVariable Long paymentId) {
        return ApiResponse.success(paymentService.getPayment(paymentId));
    }

    @PostMapping("/{paymentId}/cancel")
    public ApiResponse<PaymentResponse> cancelPayment(@PathVariable Long paymentId) {
        return ApiResponse.success("결제가 취소되었습니다.", paymentService.cancelPayment(paymentId));
    }
}
