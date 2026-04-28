import { apiRequest } from "@/api/httpClient";

export const favoriteApi = {
  createFavorite(userId, menuId) {
    return apiRequest(`/favorites?userId=${userId}`, {
      method: "POST",
      body: JSON.stringify({ menuId }),
    });
  },
  getFavorites(userId) {
    return apiRequest(`/favorites?userId=${userId}`);
  },
  deleteFavorite(userId, menuId) {
    return apiRequest(`/favorites/${menuId}?userId=${userId}`, {
      method: "DELETE",
    });
  },
};
