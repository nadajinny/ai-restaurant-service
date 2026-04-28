import { apiRequest } from "@/api/httpClient";

export const orderApi = {
  createOrder(payload) {
    return apiRequest("/orders", {
      method: "POST",
      body: JSON.stringify(payload),
    });
  },
  getOrders(userId) {
    return apiRequest(`/orders?userId=${userId}`);
  },
  getOrder(orderId, userId) {
    return apiRequest(`/orders/${orderId}?userId=${userId}`);
  },
  reorder(orderId, userId, payload = {}) {
    return apiRequest(`/orders/${orderId}/reorder?userId=${userId}`, {
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
