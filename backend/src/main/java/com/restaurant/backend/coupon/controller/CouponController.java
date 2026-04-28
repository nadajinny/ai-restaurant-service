package com.restaurant.backend.coupon.controller;

import com.restaurant.backend.common.response.ApiResponse;
import com.restaurant.backend.coupon.dto.AvailableCouponResponse;
import com.restaurant.backend.coupon.dto.CouponApplyRequest;
import com.restaurant.backend.coupon.dto.CouponApplyResponse;
import com.restaurant.backend.coupon.service.CouponService;
import com.restaurant.backend.user.service.CurrentUserService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/coupons")
public class CouponController {

    private final CouponService couponService;
    private final CurrentUserService currentUserService;

    public CouponController(CouponService couponService, CurrentUserService currentUserService) {
        this.couponService = couponService;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/available")
    public ApiResponse<List<AvailableCouponResponse>> getAvailableCoupons() {
        return ApiResponse.success(couponService.getAvailableCoupons());
    }

    @PostMapping("/apply")
    public ApiResponse<CouponApplyResponse> applyCoupon(
            @RequestParam(required = false) Long userId,
            Authentication authentication,
            @Valid @RequestBody CouponApplyRequest request
    ) {
        return ApiResponse.success(
                "쿠폰이 적용되었습니다.",
                couponService.applyCoupon(currentUserService.getCurrentUserId(authentication), request)
        );
    }
}
