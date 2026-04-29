package com.restaurant.backend.coupon.domain;

import com.restaurant.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "coupons")
public class Coupon extends BaseEntity {

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Column
    private Integer discountAmount;

    @Column
    private Integer discountRate;

    @Column
    private Integer maxDiscountAmount;

    @Column(nullable = false)
    private Integer minOrderAmount;

    @Column(nullable = false)
    private LocalDateTime availableFrom;

    @Column(nullable = false)
    private LocalDateTime availableTo;

    @Column(nullable = false)
    private Integer availableCount;

    @Column(nullable = false)
    private boolean active;

    @OneToMany(mappedBy = "coupon")
    private List<CouponUsage> couponUsages = new ArrayList<>();

    protected Coupon() {
    }

    public static Coupon create(
            String code,
            String name,
            Integer discountAmount,
            Integer discountRate,
            Integer maxDiscountAmount,
            Integer minOrderAmount,
            LocalDateTime availableFrom,
            LocalDateTime availableTo,
            Integer availableCount,
            boolean active
    ) {
        Coupon coupon = new Coupon();
        coupon.code = code;
        coupon.name = name;
        coupon.discountAmount = discountAmount;
        coupon.discountRate = discountRate;
        coupon.maxDiscountAmount = maxDiscountAmount;
        coupon.minOrderAmount = minOrderAmount;
        coupon.availableFrom = availableFrom;
        coupon.availableTo = availableTo;
        coupon.availableCount = availableCount;
        coupon.active = active;
        return coupon;
    }

    public void update(
            String code,
            String name,
            Integer discountAmount,
            Integer discountRate,
            Integer maxDiscountAmount,
            Integer minOrderAmount,
            LocalDateTime availableFrom,
            LocalDateTime availableTo,
            Integer availableCount,
            boolean active
    ) {
        this.code = code;
        this.name = name;
        this.discountAmount = discountAmount;
        this.discountRate = discountRate;
        this.maxDiscountAmount = maxDiscountAmount;
        this.minOrderAmount = minOrderAmount;
        this.availableFrom = availableFrom;
        this.availableTo = availableTo;
        this.availableCount = availableCount;
        this.active = active;
    }

    public void disable() {
        this.active = false;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public Integer getDiscountAmount() {
        return discountAmount;
    }

    public Integer getDiscountRate() {
        return discountRate;
    }

    public Integer getMaxDiscountAmount() {
        return maxDiscountAmount;
    }

    public Integer getMinOrderAmount() {
        return minOrderAmount;
    }

    public LocalDateTime getAvailableFrom() {
        return availableFrom;
    }

    public LocalDateTime getAvailableTo() {
        return availableTo;
    }

    public Integer getAvailableCount() {
        return availableCount;
    }

    public boolean isActive() {
        return active;
    }

    public List<CouponUsage> getCouponUsages() {
        return couponUsages;
    }
}
