package com.restaurant.backend.payment.controller;

import com.restaurant.backend.common.response.ApiResponse;
import com.restaurant.backend.payment.dto.PaymentCreateRequest;
import com.restaurant.backend.payment.dto.PaymentResponse;
import com.restaurant.backend.payment.service.PaymentService;
import com.restaurant.backend.user.service.CurrentUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "결제", description = "결제 생성 및 조회 API")
@SecurityRequirement(name = "bearerAuth")
public class PaymentController {

    private final PaymentService paymentService;
    private final CurrentUserService currentUserService;

    public PaymentController(PaymentService paymentService, CurrentUserService currentUserService) {
        this.paymentService = paymentService;
        this.currentUserService = currentUserService;
    }

    @PostMapping
    @Operation(summary = "결제 생성", description = "로그인한 사용자의 주문에 대한 결제를 처리합니다.")
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
    @Operation(summary = "결제 상세 조회", description = "로그인한 사용자의 결제 상세 정보를 조회합니다.")
    public ApiResponse<PaymentResponse> getPayment(@PathVariable Long paymentId, Authentication authentication) {
        return ApiResponse.success(paymentService.getPayment(currentUserService.getCurrentUserId(authentication), paymentId));
    }

    @PostMapping("/{paymentId}/cancel")
    @Operation(summary = "결제 취소", description = "로그인한 사용자의 결제를 취소합니다.")
    public ApiResponse<PaymentResponse> cancelPayment(@PathVariable Long paymentId, Authentication authentication) {
        return ApiResponse.success(
                "결제가 취소되었습니다.",
                paymentService.cancelPayment(currentUserService.getCurrentUserId(authentication), paymentId)
        );
    }
}
