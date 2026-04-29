package com.restaurant.backend.analytics.controller;

import com.restaurant.backend.analytics.dto.HourlyOrderResponse;
import com.restaurant.backend.analytics.dto.MenuPerformanceResponse;
import com.restaurant.backend.analytics.dto.PopularMenuResponse;
import com.restaurant.backend.analytics.dto.SalesAnalyticsResponse;
import com.restaurant.backend.analytics.service.AnalyticsService;
import com.restaurant.backend.common.response.ApiResponse;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/analytics")
public class AdminAnalyticsController {

    private final AnalyticsService analyticsService;

    public AdminAnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/sales")
    public ApiResponse<SalesAnalyticsResponse> getSalesAnalytics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ApiResponse.success(analyticsService.getSalesAnalytics(startDate, endDate));
    }

    @GetMapping("/popular-menus")
    public ApiResponse<List<PopularMenuResponse>> getPopularMenus() {
        return ApiResponse.success(analyticsService.getPopularMenus());
    }

    @GetMapping("/menu-performance")
    public ApiResponse<List<MenuPerformanceResponse>> getMenuPerformance() {
        return ApiResponse.success(analyticsService.getMenuPerformance());
    }

    @GetMapping("/hourly-orders")
    public ApiResponse<List<HourlyOrderResponse>> getHourlyOrders() {
        return ApiResponse.success(analyticsService.getHourlyOrders());
    }
}
