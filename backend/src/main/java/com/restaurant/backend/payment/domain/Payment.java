package com.restaurant.backend.payment.domain;

import com.restaurant.backend.common.entity.BaseEntity;
import com.restaurant.backend.order.domain.Order;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "payments")
public class Payment extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @Column(nullable = false)
    private Integer amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status;

    protected Payment() {
    }

    public static Payment create(Order order, Integer amount, PaymentStatus status) {
        Payment payment = new Payment();
        payment.order = order;
        payment.amount = amount;
        payment.status = status;
        return payment;
    }

    public void approve() {
        this.status = PaymentStatus.APPROVED;
    }

    public void fail() {
        this.status = PaymentStatus.FAILED;
    }

    public void cancel() {
        this.status = PaymentStatus.CANCELED;
    }

    public Order getOrder() {
        return order;
    }

    public Integer getAmount() {
        return amount;
    }

    public PaymentStatus getStatus() {
        return status;
    }
}
