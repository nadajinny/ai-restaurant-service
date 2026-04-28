package com.restaurant.backend.common.cache;

import java.util.List;

public final class CacheNames {

    public static final String MENUS = "menus";
    public static final String MENU_DETAILS = "menuDetails";
    public static final String POPULAR_MENUS = "popularMenus";
    public static final String REVIEW_SUMMARIES = "reviewSummaries";
    public static final String PERSONALIZED_RECOMMENDATIONS = "personalizedRecommendations";
    public static final String ADMIN_DASHBOARD = "adminDashboard";
    public static final String SALES_ANALYTICS = "salesAnalytics";
    public static final String MENU_PERFORMANCE = "menuPerformance";
    public static final String HOURLY_ORDERS = "hourlyOrders";

    public static final List<String> ALL = List.of(
            MENUS,
            MENU_DETAILS,
            POPULAR_MENUS,
            REVIEW_SUMMARIES,
            PERSONALIZED_RECOMMENDATIONS,
            ADMIN_DASHBOARD,
            SALES_ANALYTICS,
            MENU_PERFORMANCE,
            HOURLY_ORDERS
    );

    public static final List<String> ANALYTICS = List.of(
            POPULAR_MENUS,
            ADMIN_DASHBOARD,
            SALES_ANALYTICS,
            MENU_PERFORMANCE,
            HOURLY_ORDERS
    );

    private CacheNames() {
    }
}
