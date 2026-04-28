import { computed, ref } from "vue";

const STORAGE_KEY = "restaurant-customer-auth";
const authState = ref({
  accessToken: null,
  user: null,
  expiresAt: null,
});
const initialized = ref(false);

function decodeJwtPayload(token) {
  try {
    const [, payload] = token.split(".");
    return JSON.parse(window.atob(payload));
  } catch {
    return null;
  }
}

function readStorage() {
  if (typeof window === "undefined") {
    return { accessToken: null, user: null, expiresAt: null };
  }

  try {
    const raw = window.localStorage.getItem(STORAGE_KEY);
    return raw ? JSON.parse(raw) : { accessToken: null, user: null, expiresAt: null };
  } catch {
    return { accessToken: null, user: null, expiresAt: null };
  }
}

function persistAuthSession(session) {
  authState.value = session;

  if (typeof window === "undefined") {
    return;
  }

  window.localStorage.setItem(STORAGE_KEY, JSON.stringify(session));
}

function clearAuthSession() {
  authState.value = { accessToken: null, user: null, expiresAt: null };

  if (typeof window === "undefined") {
    return;
  }

  window.localStorage.removeItem(STORAGE_KEY);
}

function isExpired(expiresAt, accessToken) {
  if (expiresAt) {
    return new Date(expiresAt).getTime() <= Date.now();
  }

  const payload = accessToken ? decodeJwtPayload(accessToken) : null;
  return Boolean(payload?.exp && payload.exp * 1000 <= Date.now());
}

function initializeAuthSession() {
  if (initialized.value) {
    if (isExpired(authState.value.expiresAt, authState.value.accessToken)) {
      clearAuthSession();
    }
    return;
  }

  initialized.value = true;
  const stored = readStorage();

  if (isExpired(stored.expiresAt, stored.accessToken)) {
    clearAuthSession();
    return;
  }

  authState.value = stored;
}

function setAuthSession(loginResponse) {
  persistAuthSession({
    accessToken: loginResponse.accessToken,
    user: loginResponse.user,
    expiresAt: loginResponse.expiresAt,
  });
}

function getAccessToken() {
  initializeAuthSession();
  return authState.value.accessToken;
}

function getCurrentUser() {
  initializeAuthSession();
  return authState.value.user;
}

function redirectToLogin(redirectPath) {
  if (typeof window === "undefined") {
    return;
  }

  const nextPath = redirectPath ?? `${window.location.pathname}${window.location.search}`;
  const query = nextPath ? `?redirect=${encodeURIComponent(nextPath)}` : "";
  window.location.href = `/login${query}`;
}

const isAuthenticated = computed(() => {
  initializeAuthSession();
  return Boolean(authState.value.accessToken && authState.value.user);
});

export {
  authState,
  clearAuthSession,
  getAccessToken,
  getCurrentUser,
  initializeAuthSession,
  isAuthenticated,
  redirectToLogin,
  setAuthSession,
};
