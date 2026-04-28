import { apiRequest } from "@/api/httpClient";

export const userApi = {
  getSampleUser() {
    return apiRequest("/api/v1/users/sample");
  },
};
