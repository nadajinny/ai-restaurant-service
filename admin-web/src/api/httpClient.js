import { appConfig } from "@/config/appConfig";
import { clearAuthSession, getAccessToken, redirectToLogin } from "@/auth/authSession";

async function parseResponse(response) {
  const contentType = response.headers.get("content-type") ?? "";
  const body = contentType.includes("application/json")
    ? await response.json()
    : await response.text();

  if (!response.ok) {
    const message =
      typeof body === "object" && body !== null
        ? body.message ?? "요청 처리에 실패했습니다."
        : "요청 처리에 실패했습니다.";

    throw new Error(message);
  }

  if (
    typeof body === "object" &&
    body !== null &&
    Object.prototype.hasOwnProperty.call(body, "success")
  ) {
    if (body.success === false) {
      throw new Error(body.message ?? "요청 처리에 실패했습니다.");
    }

    return body.data;
  }

  return body;
}

export function createHttpClient(baseUrl = appConfig.apiBaseUrl) {
  return async function request(path, options = {}) {
    const accessToken = getAccessToken();
    const response = await fetch(`${baseUrl}${path}`, {
      headers: {
        "Content-Type": "application/json",
        ...(accessToken ? { Authorization: `Bearer ${accessToken}` } : {}),
        ...(options.headers ?? {}),
      },
      ...options,
    });

    if (response.status === 401) {
      clearAuthSession();
      redirectToLogin();
      throw new Error("로그인이 필요합니다.");
    }

    return parseResponse(response);
  };
}

export const apiRequest = createHttpClient();
