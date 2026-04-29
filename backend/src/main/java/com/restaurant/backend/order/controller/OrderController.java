package com.restaurant.backend.order.controller;

import com.restaurant.backend.common.response.ApiResponse;
import com.restaurant.backend.order.dto.OrderCreateRequest;
import com.restaurant.backend.order.dto.OrderCreateResponse;
import com.restaurant.backend.order.dto.OrderDetailResponse;
import com.restaurant.backend.order.dto.OrderListResponse;
import com.restaurant.backend.order.dto.ReorderRequest;
import com.restaurant.backend.order.dto.ReorderResponse;
import com.restaurant.backend.order.service.OrderService;
import com.restaurant.backend.user.service.CurrentUserService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.core.Authentication;
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
    private final CurrentUserService currentUserService;

    public OrderController(OrderService orderService, CurrentUserService currentUserService) {
        this.orderService = orderService;
        this.currentUserService = currentUserService;
    }

    @PostMapping
    public ApiResponse<OrderCreateResponse> createOrder(
            @Valid @RequestBody OrderCreateRequest request,
            Authentication authentication
    ) {
        return ApiResponse.success(
                "주문이 생성되었습니다.",
                orderService.createOrder(currentUserService.getCurrentUserId(authentication), request)
        );
    }

    @GetMapping
    public ApiResponse<List<OrderListResponse>> getOrders(
            @RequestParam(required = false) Long userId,
            Authentication authentication
    ) {
        return ApiResponse.success(orderService.getOrders(currentUserService.getCurrentUserId(authentication)));
    }

    @GetMapping("/{orderId}")
    public ApiResponse<OrderDetailResponse> getOrder(
            @PathVariable Long orderId,
            @RequestParam(required = false) Long userId,
            Authentication authentication
    ) {
        return ApiResponse.success(orderService.getOrder(orderId, currentUserService.getCurrentUserId(authentication)));
    }

    @PostMapping("/{orderId}/reorder")
    public ApiResponse<ReorderResponse> reorder(
            @PathVariable Long orderId,
            @RequestParam(required = false) Long userId,
            Authentication authentication,
            @Valid @RequestBody(required = false) ReorderRequest request
    ) {
        return ApiResponse.success(
                "재주문이 생성되었습니다.",
                orderService.reorder(orderId, currentUserService.getCurrentUserId(authentication), request)
        );
    }
}
