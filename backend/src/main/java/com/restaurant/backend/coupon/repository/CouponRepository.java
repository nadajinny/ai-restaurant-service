package com.restaurant.backend.coupon.repository;

import com.restaurant.backend.coupon.domain.Coupon;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

    Optional<Coupon> findByCode(String code);

    List<Coupon> findAllByActiveTrueAndAvailableFromLessThanEqualAndAvailableToGreaterThanEqual(
            LocalDateTime from,
            LocalDateTime to
    );
}
