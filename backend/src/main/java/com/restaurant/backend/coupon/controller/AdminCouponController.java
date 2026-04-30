package com.restaurant.backend.coupon.controller;

import com.restaurant.backend.common.response.ApiResponse;
import com.restaurant.backend.coupon.dto.AdminCouponRequest;
import com.restaurant.backend.coupon.dto.CouponResponse;
import com.restaurant.backend.coupon.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "관리자 쿠폰", description = "관리자 쿠폰 관리 API")
@SecurityRequirement(name = "bearerAuth")
public class AdminCouponController {

    private final CouponService couponService;

    public AdminCouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @GetMapping
    @Operation(summary = "쿠폰 목록 조회", description = "관리자 권한으로 전체 쿠폰 목록을 조회합니다.")
    public ApiResponse<List<CouponResponse>> getCoupons() {
        return ApiResponse.success(couponService.getCoupons());
    }

    @PostMapping
    @Operation(summary = "쿠폰 생성", description = "관리자 권한으로 신규 쿠폰을 생성합니다.")
    public ApiResponse<CouponResponse> createCoupon(@Valid @RequestBody AdminCouponRequest request) {
        return ApiResponse.success("쿠폰이 생성되었습니다.", couponService.createCoupon(request));
    }

    @PutMapping("/{couponId}")
    @Operation(summary = "쿠폰 수정", description = "관리자 권한으로 기존 쿠폰을 수정합니다.")
    public ApiResponse<CouponResponse> updateCoupon(
            @PathVariable Long couponId,
            @Valid @RequestBody AdminCouponRequest request
    ) {
        return ApiResponse.success("쿠폰이 수정되었습니다.", couponService.updateCoupon(couponId, request));
    }

    @PatchMapping("/{couponId}/disable")
    @Operation(summary = "쿠폰 비활성화", description = "관리자 권한으로 쿠폰을 비활성화합니다.")
    public ApiResponse<CouponResponse> disableCoupon(@PathVariable Long couponId) {
        return ApiResponse.success("쿠폰이 비활성화되었습니다.", couponService.disableCoupon(couponId));
    }
}
