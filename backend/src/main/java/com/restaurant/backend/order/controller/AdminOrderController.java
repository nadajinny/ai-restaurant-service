package com.restaurant.backend.order.controller;

import com.restaurant.backend.common.response.ApiResponse;
import com.restaurant.backend.order.dto.AdminOrderStatusUpdateRequest;
import com.restaurant.backend.order.dto.AdminOrderStatusUpdateResponse;
import com.restaurant.backend.order.dto.OrderDetailResponse;
import com.restaurant.backend.order.dto.OrderListResponse;
import com.restaurant.backend.order.service.AdminOrderService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/orders")
public class AdminOrderController {

    private final AdminOrderService adminOrderService;

    public AdminOrderController(AdminOrderService adminOrderService) {
        this.adminOrderService = adminOrderService;
    }

    @GetMapping
    public ApiResponse<List<OrderListResponse>> getOrders() {
        return ApiResponse.success(adminOrderService.getOrders());
    }

    @GetMapping("/{orderId}")
    public ApiResponse<OrderDetailResponse> getOrder(@PathVariable Long orderId) {
        return ApiResponse.success(adminOrderService.getOrder(orderId));
    }

    @PatchMapping("/{orderId}/status")
    public ApiResponse<AdminOrderStatusUpdateResponse> updateOrderStatus(
            @PathVariable Long orderId,
            @Valid @RequestBody AdminOrderStatusUpdateRequest request
    ) {
        return ApiResponse.success(
                "주문 상태가 변경되었습니다.",
                adminOrderService.updateOrderStatus(orderId, request)
        );
    }
}
