package com.restaurant.backend.coupon.controller;

import com.restaurant.backend.common.response.ApiResponse;
import com.restaurant.backend.coupon.dto.AdminCouponRequest;
import com.restaurant.backend.coupon.dto.CouponResponse;
import com.restaurant.backend.coupon.service.CouponService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/coupons")
public class AdminCouponController {

    private final CouponService couponService;

    public AdminCouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @GetMapping
    public ApiResponse<List<CouponResponse>> getCoupons() {
        return ApiResponse.success(couponService.getCoupons());
    }

    @PostMapping
    public ApiResponse<CouponResponse> createCoupon(@Valid @RequestBody AdminCouponRequest request) {
        return ApiResponse.success("쿠폰이 생성되었습니다.", couponService.createCoupon(request));
    }

    @PutMapping("/{couponId}")
    public ApiResponse<CouponResponse> updateCoupon(
            @PathVariable Long couponId,
            @Valid @RequestBody AdminCouponRequest request
    ) {
        return ApiResponse.success("쿠폰이 수정되었습니다.", couponService.updateCoupon(couponId, request));
    }

    @PatchMapping("/{couponId}/disable")
    public ApiResponse<CouponResponse> disableCoupon(@PathVariable Long couponId) {
        return ApiResponse.success("쿠폰이 비활성화되었습니다.", couponService.disableCoupon(couponId));
    }
}
