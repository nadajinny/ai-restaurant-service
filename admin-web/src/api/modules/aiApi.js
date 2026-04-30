import { apiRequest } from "@/api/httpClient";

export const aiApi = {
  recommend(payload) {
    return apiRequest("/ai/recommend", {
      method: "POST",
      body: JSON.stringify(payload),
    });
  },
  getPersonalizedRecommendations() {
    return apiRequest("/ai/personalized-recommendations");
  },
  emotionRecommend(payload) {
    return apiRequest("/ai/emotion-recommend", {
      method: "POST",
      body: JSON.stringify(payload),
    });
  },
  generateReview(payload) {
    return apiRequest("/ai/review-generate", {
      method: "POST",
      body: JSON.stringify(payload),
    });
  },
  getReviewSummary(menuId) {
    return apiRequest(`/ai/menus/${menuId}/review-summary`);
  },
  getNewMenuRecommendations() {
    return apiRequest("/admin/ai/new-menu-recommendations");
  },
};
