package com.restaurant.backend.analytics.controller;

import com.restaurant.backend.analytics.dto.AnalyticsSummaryDto;
import com.restaurant.backend.analytics.service.AnalyticsService;
import com.restaurant.backend.common.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/sample")
    public ApiResponse<AnalyticsSummaryDto> getSampleAnalytics() {
        return ApiResponse.success(analyticsService.getSampleAnalytics());
    }
}
