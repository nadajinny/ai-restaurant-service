import { apiRequest } from "@/api/httpClient";

export const reviewApi = {
  createReview(payload) {
    return apiRequest("/reviews", {
      method: "POST",
      body: JSON.stringify(payload),
    });
  },
  getMenuReviews(menuId) {
    return apiRequest(`/menus/${menuId}/reviews`);
  },
  updateReview(reviewId, payload) {
    return apiRequest(`/reviews/${reviewId}`, {
      method: "PUT",
      body: JSON.stringify(payload),
    });
  },
  deleteReview(reviewId) {
    return apiRequest(`/reviews/${reviewId}`, {
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
