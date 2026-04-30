package com.restaurant.backend.analytics.controller;

import com.restaurant.backend.analytics.dto.DashboardResponse;
import com.restaurant.backend.analytics.service.AnalyticsService;
import com.restaurant.backend.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@Tag(name = "관리자 대시보드", description = "관리자 대시보드 API")
@SecurityRequirement(name = "bearerAuth")
public class AdminDashboardController {

    private final AnalyticsService analyticsService;

    public AdminDashboardController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/dashboard")
    @Operation(summary = "대시보드 조회", description = "관리자 권한으로 대시보드 요약 정보를 조회합니다.")
    public ApiResponse<DashboardResponse> getDashboard() {
        return ApiResponse.success(analyticsService.getDashboard());
    }
}
