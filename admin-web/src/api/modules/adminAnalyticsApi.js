import { apiRequest } from "@/api/httpClient";

export const adminAnalyticsApi = {
  getDashboard() {
    return apiRequest("/admin/dashboard");
  },
  getSalesAnalytics() {
    return apiRequest("/admin/analytics/sales");
  },
  getPopularMenus() {
    return apiRequest("/admin/analytics/popular-menus");
  },
  getMenuPerformance() {
    return apiRequest("/admin/analytics/menu-performance");
  },
  getHourlyOrders() {
    return apiRequest("/admin/analytics/hourly-orders");
  },
};
