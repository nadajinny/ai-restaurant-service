import { apiRequest } from "@/api/httpClient";

export const adminMenuApi = {
  getMenus() {
    return apiRequest("/admin/menus");
  },
  createMenu(payload) {
    return apiRequest("/admin/menus", {
      method: "POST",
      body: JSON.stringify(payload),
    });
  },
  updateMenu(menuId, payload) {
    return apiRequest(`/admin/menus/${menuId}`, {
      method: "PUT",
      body: JSON.stringify(payload),
    });
  },
  deleteMenu(menuId) {
    return apiRequest(`/admin/menus/${menuId}`, {
      method: "DELETE",
    });
  },
  updateMenuStatus(menuId, status) {
    return apiRequest(`/admin/menus/${menuId}/status`, {
      method: "PATCH",
      body: JSON.stringify({ status }),
    });
  },
};
