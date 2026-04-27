package com.restaurant.backend.order.service;

import com.restaurant.backend.order.domain.Order;
import com.restaurant.backend.order.domain.OrderItem;
import com.restaurant.backend.order.dto.OrderCreateResponse;
import com.restaurant.backend.order.dto.OrderDetailItemResponse;
import com.restaurant.backend.order.dto.OrderDetailResponse;
import com.restaurant.backend.order.dto.OrderListResponse;
import com.restaurant.backend.order.dto.ReorderResponse;
import com.restaurant.backend.order.dto.ReorderUnavailableItemResponse;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {

    public OrderCreateResponse toOrderCreateResponse(Order order) {
        return new OrderCreateResponse(order.getId(), order.getStatus(), order.getTotalPrice());
    }

    public ReorderResponse toReorderResponse(Order order, List<ReorderUnavailableItemResponse> unavailableItems) {
        return new ReorderResponse(order.getId(), order.getStatus(), order.getTotalPrice(), unavailableItems);
    }

    public OrderListResponse toOrderListResponse(Order order) {
        String representativeMenuName = order.getOrderItems().stream()
                .sorted(Comparator.comparing(OrderItem::getId))
                .findFirst()
                .map(orderItem -> orderItem.getMenu().getName())
                .orElse("");

        return new OrderListResponse(
                order.getId(),
                order.getCreatedAt(),
                order.getTotalPrice(),
                order.getStatus(),
                representativeMenuName
        );
    }

    public OrderDetailResponse toOrderDetailResponse(Order order) {
        List<OrderDetailItemResponse> items = order.getOrderItems().stream()
                .sorted(Comparator.comparing(OrderItem::getId))
                .map(this::toOrderDetailItemResponse)
                .toList();

        return new OrderDetailResponse(
                order.getId(),
                order.getCreatedAt(),
                order.getTotalPrice(),
                order.getStatus(),
                items
        );
    }

    private OrderDetailItemResponse toOrderDetailItemResponse(OrderItem orderItem) {
        return new OrderDetailItemResponse(
                orderItem.getMenu().getId(),
                orderItem.getMenu().getName(),
                orderItem.getQuantity(),
                orderItem.getItemPrice()
        );
    }
}
