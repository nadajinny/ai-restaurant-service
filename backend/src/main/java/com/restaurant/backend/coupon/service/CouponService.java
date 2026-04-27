package com.restaurant.backend.coupon.service;

import com.restaurant.backend.coupon.dto.CouponSummaryDto;
import org.springframework.stereotype.Service;

@Service
public class CouponService {

    public CouponSummaryDto getSampleCoupon() {
        return new CouponSummaryDto(1L, "WELCOME1000", 1000, "ACTIVE");
    }
}
