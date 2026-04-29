import { apiRequest } from "@/api/httpClient";

export const orderApi = {
  createOrder(payload) {
    return apiRequest("/orders", {
      method: "POST",
      body: JSON.stringify(payload),
    });
  },
  getOrders() {
    return apiRequest("/orders");
  },
  getOrder(orderId) {
    return apiRequest(`/orders/${orderId}`);
  },
  reorder(orderId, payload = {}) {
    return apiRequest(`/orders/${orderId}/reorder`, {
      method: "POST",
      body: JSON.stringify(payload),
    });
  },
  updateStatus(orderId, status) {
    return apiRequest(`/admin/orders/${orderId}/status`, {
      method: "PATCH",
      body: JSON.stringify({ status }),
    });
  },
};
