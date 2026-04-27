package com.restaurant.backend.order.controller;

import com.restaurant.backend.common.response.ApiResponse;
import com.restaurant.backend.order.dto.OrderCreateRequest;
import com.restaurant.backend.order.dto.OrderCreateResponse;
import com.restaurant.backend.order.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
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
}
