import { apiRequest } from "@/api/httpClient";

export const adminCouponApi = {
  getCoupons() {
    return apiRequest("/admin/coupons");
  },
  createCoupon(payload) {
    return apiRequest("/admin/coupons", {
      method: "POST",
      body: JSON.stringify(payload),
    });
  },
  updateCoupon(couponId, payload) {
    return apiRequest(`/admin/coupons/${couponId}`, {
      method: "PUT",
      body: JSON.stringify(payload),
    });
  },
  disableCoupon(couponId) {
    return apiRequest(`/admin/coupons/${couponId}/disable`, {
      method: "PATCH",
    });
  },
  getAvailableCoupons() {
    return apiRequest("/coupons/available");
  },
  applyCoupon(payload) {
    return apiRequest("/coupons/apply", {
      method: "POST",
      body: JSON.stringify(payload),
    });
  },
};
