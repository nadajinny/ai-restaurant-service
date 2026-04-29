package com.restaurant.backend.order.domain;

import com.restaurant.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "order_status_histories")
public class OrderStatusHistory extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private OrderStatus fromStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus toStatus;

    @Column(length = 100)
    private String changedBy;

    protected OrderStatusHistory() {
    }

    public static OrderStatusHistory create(
            Order order,
            OrderStatus fromStatus,
            OrderStatus toStatus,
            String changedBy
    ) {
        OrderStatusHistory history = new OrderStatusHistory();
        history.order = order;
        history.fromStatus = fromStatus;
        history.toStatus = toStatus;
        history.changedBy = changedBy;
        return history;
    }

    public Order getOrder() {
        return order;
    }

    public OrderStatus getFromStatus() {
        return fromStatus;
    }

    public OrderStatus getToStatus() {
        return toStatus;
    }

    public String getChangedBy() {
        return changedBy;
    }
}
