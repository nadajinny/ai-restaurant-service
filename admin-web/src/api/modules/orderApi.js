import { apiRequest } from "@/api/httpClient";

export const orderApi = {
  getOrders() {
    return apiRequest("/admin/orders");
  },
  getOrder(orderId) {
    return apiRequest(`/admin/orders/${orderId}`);
  },
  updateStatus(orderId, status) {
    return apiRequest(`/admin/orders/${orderId}/status`, {
      method: "PATCH",
      body: JSON.stringify({ status }),
    });
  },
};
