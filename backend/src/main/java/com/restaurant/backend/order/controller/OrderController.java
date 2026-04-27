package com.restaurant.backend.order.controller;

import com.restaurant.backend.common.response.ApiResponse;
import com.restaurant.backend.order.dto.OrderCreateRequest;
import com.restaurant.backend.order.dto.OrderCreateResponse;
import com.restaurant.backend.order.dto.OrderDetailResponse;
import com.restaurant.backend.order.dto.OrderListResponse;
import com.restaurant.backend.order.dto.ReorderRequest;
import com.restaurant.backend.order.dto.ReorderResponse;
import com.restaurant.backend.order.service.OrderService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ApiResponse<OrderCreateResponse> createOrder(@Valid @RequestBody OrderCreateRequest request) {
        return ApiResponse.success("주문이 생성되었습니다.", orderService.createOrder(request));
    }

    @GetMapping
    public ApiResponse<List<OrderListResponse>> getOrders(@RequestParam Long userId) {
        // TODO: 인증 기능 구현 후 userId 요청 파라미터 대신 JWT 기반 사용자 식별로 대체한다.
        return ApiResponse.success(orderService.getOrders(userId));
    }

    @GetMapping("/{orderId}")
    public ApiResponse<OrderDetailResponse> getOrder(
            @PathVariable Long orderId,
            @RequestParam Long userId
    ) {
        // TODO: 인증 기능 구현 후 userId 요청 파라미터 대신 JWT 기반 사용자 식별로 대체한다.
        return ApiResponse.success(orderService.getOrder(orderId, userId));
    }

    @PostMapping("/{orderId}/reorder")
    public ApiResponse<ReorderResponse> reorder(
            @PathVariable Long orderId,
            @RequestParam Long userId,
            @Valid @RequestBody(required = false) ReorderRequest request
    ) {
        // TODO: 인증 기능 구현 후 userId 요청 파라미터 대신 JWT 기반 사용자 식별로 대체한다.
        return ApiResponse.success("재주문이 생성되었습니다.", orderService.reorder(orderId, userId, request));
    }
}
