package com.restaurant.backend.order.repository;

import com.restaurant.backend.order.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
