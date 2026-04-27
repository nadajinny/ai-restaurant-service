package com.restaurant.backend.coupon.repository;

import com.restaurant.backend.coupon.domain.CouponUsage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponUsageRepository extends JpaRepository<CouponUsage, Long> {
}
