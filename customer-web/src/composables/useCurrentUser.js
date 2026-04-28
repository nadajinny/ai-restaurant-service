import { userApi } from "@/api";
import { ref } from "vue";

const STORAGE_KEY = "restaurant-current-user";
const currentUser = ref(null);
const initialized = ref(false);

function readStorage() {
  if (typeof window === "undefined") {
    return null;
  }

  try {
    const raw = window.localStorage.getItem(STORAGE_KEY);
    return raw ? JSON.parse(raw) : null;
  } catch {
    return null;
  }
}

function persist(user) {
  if (typeof window === "undefined") {
    return;
  }

  window.localStorage.setItem(STORAGE_KEY, JSON.stringify(user));
}

async function ensureCurrentUser() {
  if (!initialized.value) {
    currentUser.value = readStorage();
    initialized.value = true;
  }

  if (currentUser.value?.id) {
    return currentUser.value;
  }

  const user = await userApi.getSampleUser();
  currentUser.value = user;
  persist(user);
  return user;
}

export function useCurrentUser() {
  return {
    currentUser,
    ensureCurrentUser,
  };
}
