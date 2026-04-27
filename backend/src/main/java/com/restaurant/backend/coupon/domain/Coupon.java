package com.restaurant.backend.coupon.domain;

import com.restaurant.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "coupons")
public class Coupon extends BaseEntity {

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private Integer discountAmount;

    @Column(nullable = false)
    private boolean active;

    @OneToMany(mappedBy = "coupon")
    private List<CouponUsage> couponUsages = new ArrayList<>();

    protected Coupon() {
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

    public boolean isActive() {
        return active;
    }

    public List<CouponUsage> getCouponUsages() {
        return couponUsages;
    }
}
