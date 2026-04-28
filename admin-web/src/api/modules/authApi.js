import { apiRequest } from "@/api/httpClient";

export const authApi = {
  login(payload) {
    return apiRequest("/auth/login", {
      method: "POST",
      body: JSON.stringify(payload),
    });
  },
};
