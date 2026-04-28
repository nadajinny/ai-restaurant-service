import { apiRequest } from "@/api/httpClient";

export const notificationApi = {
  getNotifications(userId) {
    return apiRequest(`/notifications?userId=${userId}`);
  },
  readNotification(notificationId, userId) {
    return apiRequest(`/notifications/${notificationId}/read?userId=${userId}`, {
      method: "PATCH",
    });
  },
};
