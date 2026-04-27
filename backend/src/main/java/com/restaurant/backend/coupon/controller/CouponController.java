package com.restaurant.backend.coupon.controller;

import com.restaurant.backend.common.response.ApiResponse;
import com.restaurant.backend.coupon.dto.CouponSummaryDto;
import com.restaurant.backend.coupon.service.CouponService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/coupons")
public class CouponController {

    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @GetMapping("/sample")
    public ApiResponse<CouponSummaryDto> getSampleCoupon() {
        return ApiResponse.success(couponService.getSampleCoupon());
    }
}
