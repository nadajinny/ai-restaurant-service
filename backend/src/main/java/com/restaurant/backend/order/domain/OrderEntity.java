package com.restaurant.backend.order.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "orders")
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Integer totalPrice;
    private String status;

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Integer getTotalPrice() {
        return totalPrice;
    }

    public String getStatus() {
        return status;
    }
}
