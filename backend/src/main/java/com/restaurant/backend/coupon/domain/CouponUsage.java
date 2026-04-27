package com.restaurant.backend.coupon.domain;

import com.restaurant.backend.common.entity.BaseEntity;
import com.restaurant.backend.order.domain.Order;
import com.restaurant.backend.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "coupon_usages")
public class CouponUsage extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id", nullable = false)
    private Coupon coupon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(nullable = false)
    private Integer discountAmount;

    protected CouponUsage() {
    }

    public Coupon getCoupon() {
        return coupon;
    }

    public User getUser() {
        return user;
    }

    public Order getOrder() {
        return order;
    }

    public Integer getDiscountAmount() {
        return discountAmount;
    }
}
