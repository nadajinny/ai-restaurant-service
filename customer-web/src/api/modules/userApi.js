import { apiRequest } from "@/api/httpClient";

export const userApi = {
  getCurrentUser() {
    return apiRequest("/api/v1/users/me");
  },
};
