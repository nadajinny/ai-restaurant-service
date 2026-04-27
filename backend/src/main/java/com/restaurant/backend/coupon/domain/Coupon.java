package com.restaurant.backend.coupon.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "coupons")
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;
    private Integer discountAmount;
    private String status;

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public Integer getDiscountAmount() {
        return discountAmount;
    }

    public String getStatus() {
        return status;
    }
}
