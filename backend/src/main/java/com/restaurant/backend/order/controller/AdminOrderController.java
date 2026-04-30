package com.restaurant.backend.order.controller;

import com.restaurant.backend.common.response.ApiResponse;
import com.restaurant.backend.order.dto.AdminOrderStatusUpdateRequest;
import com.restaurant.backend.order.dto.AdminOrderStatusUpdateResponse;
import com.restaurant.backend.order.dto.OrderDetailResponse;
import com.restaurant.backend.order.dto.OrderListResponse;
import com.restaurant.backend.order.service.AdminOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "관리자 주문", description = "관리자 주문 관리 API")
@SecurityRequirement(name = "bearerAuth")
public class AdminOrderController {

    private final AdminOrderService adminOrderService;

    public AdminOrderController(AdminOrderService adminOrderService) {
        this.adminOrderService = adminOrderService;
    }

    @GetMapping
    @Operation(summary = "전체 주문 목록 조회", description = "관리자 권한으로 전체 주문 목록을 조회합니다.")
    public ApiResponse<List<OrderListResponse>> getOrders() {
        return ApiResponse.success(adminOrderService.getOrders());
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "주문 상세 조회", description = "관리자 권한으로 주문 상세 정보를 조회합니다.")
    public ApiResponse<OrderDetailResponse> getOrder(@PathVariable Long orderId) {
        return ApiResponse.success(adminOrderService.getOrder(orderId));
    }

    @PatchMapping("/{orderId}/status")
    @Operation(summary = "주문 상태 변경", description = "관리자 권한으로 주문 상태를 변경합니다.")
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
