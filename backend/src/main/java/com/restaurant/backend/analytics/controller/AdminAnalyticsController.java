package com.restaurant.backend.analytics.controller;

import com.restaurant.backend.analytics.dto.HourlyOrderResponse;
import com.restaurant.backend.analytics.dto.MenuPerformanceResponse;
import com.restaurant.backend.analytics.dto.PopularMenuResponse;
import com.restaurant.backend.analytics.dto.SalesAnalyticsResponse;
import com.restaurant.backend.analytics.service.AnalyticsService;
import com.restaurant.backend.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/analytics")
@Tag(name = "관리자 분석", description = "관리자 분석 및 통계 API")
@SecurityRequirement(name = "bearerAuth")
public class AdminAnalyticsController {

    private final AnalyticsService analyticsService;

    public AdminAnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/sales")
    @Operation(summary = "매출 분석 조회", description = "기간 조건으로 매출 분석 데이터를 조회합니다.")
    public ApiResponse<SalesAnalyticsResponse> getSalesAnalytics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ApiResponse.success(analyticsService.getSalesAnalytics(startDate, endDate));
    }

    @GetMapping("/popular-menus")
    @Operation(summary = "인기 메뉴 분석 조회", description = "판매량 기준 인기 메뉴 분석 결과를 조회합니다.")
    public ApiResponse<List<PopularMenuResponse>> getPopularMenus() {
        return ApiResponse.success(analyticsService.getPopularMenus());
    }

    @GetMapping("/menu-performance")
    @Operation(summary = "메뉴 성과 분석 조회", description = "메뉴별 성과 지표를 조회합니다.")
    public ApiResponse<List<MenuPerformanceResponse>> getMenuPerformance() {
        return ApiResponse.success(analyticsService.getMenuPerformance());
    }

    @GetMapping("/hourly-orders")
    @Operation(summary = "시간대별 주문 분석 조회", description = "시간대별 주문량 분석 결과를 조회합니다.")
    public ApiResponse<List<HourlyOrderResponse>> getHourlyOrders() {
        return ApiResponse.success(analyticsService.getHourlyOrders());
    }
}
