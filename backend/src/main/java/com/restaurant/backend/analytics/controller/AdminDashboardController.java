package com.restaurant.backend.analytics.controller;

import com.restaurant.backend.analytics.dto.DashboardResponse;
import com.restaurant.backend.analytics.service.AnalyticsService;
import com.restaurant.backend.common.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminDashboardController {

    private final AnalyticsService analyticsService;

    public AdminDashboardController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/dashboard")
    public ApiResponse<DashboardResponse> getDashboard() {
        return ApiResponse.success(analyticsService.getDashboard());
    }
}
