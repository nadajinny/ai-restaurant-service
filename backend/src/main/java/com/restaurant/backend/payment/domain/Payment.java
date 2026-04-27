package com.restaurant.backend.payment.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;
    private Integer amount;
    private String status;

    public Long getId() {
        return id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Integer getAmount() {
        return amount;
    }

    public String getStatus() {
        return status;
    }
}
