import { apiRequest } from "@/api/httpClient";

export const adminAnalyticsApi = {
  getDashboard() {
    return apiRequest("/admin/dashboard");
  },
  getSalesAnalytics(params = {}) {
    const query = new URLSearchParams();

    Object.entries(params).forEach(([key, value]) => {
      if (value !== undefined && value !== null && value !== "") {
        query.set(key, value);
      }
    });

    const suffix = query.toString() ? `?${query.toString()}` : "";
    return apiRequest(`/admin/analytics/sales${suffix}`);
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
