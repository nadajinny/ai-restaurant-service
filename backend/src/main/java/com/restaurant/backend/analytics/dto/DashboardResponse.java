package com.restaurant.backend.analytics.dto;

import java.util.List;

public record DashboardResponse(
        long todayOrderCount,
        int todaySales,
        List<PopularMenuResponse> popularMenus,
        List<HourlyOrderResponse> hourlyOrders,
        List<SoldOutMenuResponse> soldOutMenus,
        List<RecentReviewResponse> recentReviews
) {
}
