import { apiRequest } from "@/api/httpClient";

export const adminInventoryApi = {
  getInventories() {
    return apiRequest("/admin/inventories");
  },
  updateInventory(menuId, quantity) {
    return apiRequest(`/admin/inventories/${menuId}`, {
      method: "PUT",
      body: JSON.stringify({ quantity }),
    });
  },
  markSoldOut(menuId) {
    return apiRequest(`/admin/inventories/${menuId}/sold-out`, {
      method: "PATCH",
    });
  },
  markAvailable(menuId) {
    return apiRequest(`/admin/inventories/${menuId}/available`, {
      method: "PATCH",
    });
  },
};
