package com.restaurant.backend.order.domain;

import com.restaurant.backend.common.entity.BaseEntity;
import com.restaurant.backend.payment.domain.Payment;
import com.restaurant.backend.review.domain.Review;
import com.restaurant.backend.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Integer totalPrice;

    @Column(nullable = false)
    private Integer originalTotalPrice;

    @Column(nullable = false)
    private Integer discountAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status;

    @OneToMany(mappedBy = "order")
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToMany(mappedBy = "order")
    private List<Review> reviews = new ArrayList<>();

    @OneToOne(mappedBy = "order")
    private Payment payment;

    @OneToMany(mappedBy = "order")
    private List<OrderStatusHistory> statusHistories = new ArrayList<>();

    protected Order() {
    }

    public static Order create(User user, Integer totalPrice, OrderStatus status) {
        Order order = new Order();
        order.user = user;
        order.totalPrice = totalPrice;
        order.originalTotalPrice = totalPrice;
        order.discountAmount = 0;
        order.status = status;
        return order;
    }

    public boolean canTransitionTo(OrderStatus nextStatus) {
        return switch (status) {
            case RECEIVED -> nextStatus == OrderStatus.COOKING || nextStatus == OrderStatus.CANCELED;
            case COOKING -> nextStatus == OrderStatus.READY;
            case READY -> nextStatus == OrderStatus.COMPLETED;
            case COMPLETED, CANCELED -> false;
        };
    }

    public void changeStatus(OrderStatus nextStatus) {
        this.status = nextStatus;
    }

    public void applyDiscount(Integer discountAmount) {
        this.discountAmount = discountAmount;
        this.totalPrice = Math.max(0, this.originalTotalPrice - discountAmount);
    }

    public void clearDiscount() {
        this.discountAmount = 0;
        this.totalPrice = this.originalTotalPrice;
    }

    public User getUser() {
        return user;
    }

    public Integer getTotalPrice() {
        return totalPrice;
    }

    public Integer getOriginalTotalPrice() {
        return originalTotalPrice;
    }

    public Integer getDiscountAmount() {
        return discountAmount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public Payment getPayment() {
        return payment;
    }

    public List<OrderStatusHistory> getStatusHistories() {
        return statusHistories;
    }
}
