import { apiRequest } from "@/api/httpClient";

export const menuApi = {
  getMenus(params = {}) {
    const query = new URLSearchParams();
    Object.entries(params).forEach(([key, value]) => {
      if (value !== undefined && value !== null && value !== "") {
        query.set(key, value);
      }
    });

    const suffix = query.toString() ? `?${query.toString()}` : "";
    return apiRequest(`/menus${suffix}`);
  },
  getMenu(menuId) {
    return apiRequest(`/menus/${menuId}`);
  },
};
