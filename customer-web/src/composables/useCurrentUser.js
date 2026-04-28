import {
  authState,
  getCurrentUser,
  initializeAuthSession,
  isAuthenticated,
  redirectToLogin,
} from "@/auth/authSession";
import { computed } from "vue";

async function ensureCurrentUser(options = {}) {
  const { redirect = true } = options;

  initializeAuthSession();

  const user = getCurrentUser();
  if (user?.id) {
    return user;
  }

  if (redirect) {
    redirectToLogin();
  }

  throw new Error("로그인이 필요합니다.");
}

export function useCurrentUser() {
  return {
    currentUser: computed(() => authState.value.user),
    isAuthenticated,
    getCurrentUser,
    ensureCurrentUser,
  };
}
