package com.restaurant.backend.payment.controller;

import com.restaurant.backend.common.response.ApiResponse;
import com.restaurant.backend.payment.dto.PaymentCreateRequest;
import com.restaurant.backend.payment.dto.PaymentResponse;
import com.restaurant.backend.payment.service.PaymentService;
import com.restaurant.backend.user.service.CurrentUserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
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
    private final CurrentUserService currentUserService;

    public PaymentController(PaymentService paymentService, CurrentUserService currentUserService) {
        this.paymentService = paymentService;
        this.currentUserService = currentUserService;
    }

    @PostMapping
    public ApiResponse<PaymentResponse> createPayment(
            @Valid @RequestBody PaymentCreateRequest request,
            Authentication authentication
    ) {
        return ApiResponse.success(
                "결제가 처리되었습니다.",
                paymentService.createPayment(currentUserService.getCurrentUserId(authentication), request)
        );
    }

    @GetMapping("/{paymentId}")
    public ApiResponse<PaymentResponse> getPayment(@PathVariable Long paymentId, Authentication authentication) {
        return ApiResponse.success(paymentService.getPayment(currentUserService.getCurrentUserId(authentication), paymentId));
    }

    @PostMapping("/{paymentId}/cancel")
    public ApiResponse<PaymentResponse> cancelPayment(@PathVariable Long paymentId, Authentication authentication) {
        return ApiResponse.success(
                "결제가 취소되었습니다.",
                paymentService.cancelPayment(currentUserService.getCurrentUserId(authentication), paymentId)
        );
    }
}
