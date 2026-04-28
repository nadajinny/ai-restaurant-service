import { apiRequest } from "@/api/httpClient";

export const favoriteApi = {
  createFavorite(menuId) {
    return apiRequest("/favorites", {
      method: "POST",
      body: JSON.stringify({ menuId }),
    });
  },
  getFavorites() {
    return apiRequest("/favorites");
  },
  deleteFavorite(menuId) {
    return apiRequest(`/favorites/${menuId}`, {
      method: "DELETE",
    });
  },
};
