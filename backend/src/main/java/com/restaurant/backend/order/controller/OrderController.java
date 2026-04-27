package com.restaurant.backend.order.controller;

import com.restaurant.backend.common.response.ApiResponse;
import com.restaurant.backend.order.dto.OrderSummaryDto;
import com.restaurant.backend.order.service.OrderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/sample")
    public ApiResponse<OrderSummaryDto> getSampleOrder() {
        return ApiResponse.success(orderService.getSampleOrder());
    }
}
