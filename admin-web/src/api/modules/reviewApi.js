import { apiRequest } from "@/api/httpClient";

export const reviewApi = {
  createReview(userId, payload) {
    return apiRequest(`/reviews?userId=${userId}`, {
      method: "POST",
      body: JSON.stringify(payload),
    });
  },
  getMenuReviews(menuId) {
    return apiRequest(`/menus/${menuId}/reviews`);
  },
  updateReview(reviewId, userId, payload) {
    return apiRequest(`/reviews/${reviewId}?userId=${userId}`, {
      method: "PUT",
      body: JSON.stringify(payload),
    });
  },
  deleteReview(reviewId, userId) {
    return apiRequest(`/reviews/${reviewId}?userId=${userId}`, {
      method: "DELETE",
    });
  },
  getAdminReviews() {
    return apiRequest("/admin/reviews");
  },
  hideReview(reviewId) {
    return apiRequest(`/admin/reviews/${reviewId}/hide`, {
      method: "PATCH",
    });
  },
};
