package com.restaurant.backend.coupon.repository;

import com.restaurant.backend.coupon.domain.CouponUsage;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponUsageRepository extends JpaRepository<CouponUsage, Long> {

    long countByCoupon_Id(Long couponId);

    boolean existsByCoupon_IdAndUser_Id(Long couponId, Long userId);

    boolean existsByOrder_Id(Long orderId);

    Optional<CouponUsage> findByOrder_Id(Long orderId);

    List<CouponUsage> findAllByCoupon_Id(Long couponId);
}
