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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
@Tag(name = "주문", description = "고객 주문 생성 및 조회 API")
@SecurityRequirement(name = "bearerAuth")
public class OrderController {

    private final OrderService orderService;
    private final CurrentUserService currentUserService;

    public OrderController(OrderService orderService, CurrentUserService currentUserService) {
        this.orderService = orderService;
        this.currentUserService = currentUserService;
    }

    @PostMapping
    @Operation(summary = "주문 생성", description = "로그인한 사용자의 신규 주문을 생성합니다.")
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
    @Operation(summary = "내 주문 목록 조회", description = "로그인한 사용자의 주문 목록을 조회합니다.")
    public ApiResponse<List<OrderListResponse>> getOrders(Authentication authentication) {
        return ApiResponse.success(orderService.getOrders(currentUserService.getCurrentUserId(authentication)));
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "주문 상세 조회", description = "로그인한 사용자의 주문 상세 정보를 조회합니다.")
    public ApiResponse<OrderDetailResponse> getOrder(
            @PathVariable Long orderId,
            Authentication authentication
    ) {
        return ApiResponse.success(orderService.getOrder(orderId, currentUserService.getCurrentUserId(authentication)));
    }

    @PostMapping("/{orderId}/reorder")
    @Operation(summary = "재주문 생성", description = "이전 주문을 기반으로 재주문을 생성합니다.")
    public ApiResponse<ReorderResponse> reorder(
            @PathVariable Long orderId,
            Authentication authentication,
            @Valid @RequestBody(required = false) ReorderRequest request
    ) {
        return ApiResponse.success(
                "재주문이 생성되었습니다.",
                orderService.reorder(orderId, currentUserService.getCurrentUserId(authentication), request)
        );
    }
}
