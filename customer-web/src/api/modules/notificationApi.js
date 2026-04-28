import { apiRequest } from "@/api/httpClient";

export const notificationApi = {
  getNotifications() {
    return apiRequest("/notifications");
  },
  readNotification(notificationId) {
    return apiRequest(`/notifications/${notificationId}/read`, {
      method: "PATCH",
    });
  },
};
