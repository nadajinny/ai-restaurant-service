package com.restaurant.backend.coupon.controller;

import com.restaurant.backend.common.response.ApiResponse;
import com.restaurant.backend.coupon.dto.AvailableCouponResponse;
import com.restaurant.backend.coupon.dto.CouponApplyRequest;
import com.restaurant.backend.coupon.dto.CouponApplyResponse;
import com.restaurant.backend.coupon.service.CouponService;
import com.restaurant.backend.user.service.CurrentUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/coupons")
@Tag(name = "쿠폰", description = "쿠폰 조회 및 적용 API")
public class CouponController {

    private final CouponService couponService;
    private final CurrentUserService currentUserService;

    public CouponController(CouponService couponService, CurrentUserService currentUserService) {
        this.couponService = couponService;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/available")
    @Operation(summary = "사용 가능 쿠폰 조회", description = "현재 적용 가능한 쿠폰 목록을 조회합니다.")
    public ApiResponse<List<AvailableCouponResponse>> getAvailableCoupons() {
        return ApiResponse.success(couponService.getAvailableCoupons());
    }

    @PostMapping("/apply")
    @Operation(summary = "쿠폰 적용", description = "로그인한 사용자의 주문에 쿠폰을 적용합니다.")
    @SecurityRequirement(name = "bearerAuth")
    public ApiResponse<CouponApplyResponse> applyCoupon(
            Authentication authentication,
            @Valid @RequestBody CouponApplyRequest request
    ) {
        return ApiResponse.success(
                "쿠폰이 적용되었습니다.",
                couponService.applyCoupon(currentUserService.getCurrentUserId(authentication), request)
        );
    }
}
