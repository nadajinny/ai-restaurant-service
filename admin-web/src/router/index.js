import { createRouter, createWebHistory } from "vue-router";
import { clearAuthSession, getCurrentUser, initializeAuthSession, isAuthenticated } from "@/auth/authSession";
import { adminRoutes } from "./modules/adminRoutes";

const router = createRouter({
  history: createWebHistory(),
  routes: [
    ...adminRoutes,
    {
      path: "/:pathMatch(.*)*",
      redirect: "/admin",
    },
  ],
  scrollBehavior() {
    return { top: 0 };
  },
});

router.beforeEach((to) => {
  initializeAuthSession();

  if (to.meta?.requiresAuth && !isAuthenticated.value) {
    return {
      path: "/admin/login",
      query: { redirect: to.fullPath },
    };
  }

  if (to.meta?.requiresAuth) {
    const user = getCurrentUser();
    if (to.meta.requiredRole && user?.role !== to.meta.requiredRole) {
      clearAuthSession();
      return "/admin/login";
    }
  }

  if (to.meta?.guestOnly && isAuthenticated.value) {
    const user = getCurrentUser();
    if (user?.role === "ADMIN") {
      return "/admin";
    }

    clearAuthSession();
  }

  return true;
});

export default router;
