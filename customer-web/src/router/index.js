import { createRouter, createWebHistory } from "vue-router";
import { getCurrentUser, initializeAuthSession, isAuthenticated } from "@/auth/authSession";
import { customerRoutes } from "./modules/customerRoutes";

const router = createRouter({
  history: createWebHistory(),
  routes: [
    ...customerRoutes,
    {
      path: "/:pathMatch(.*)*",
      redirect: "/",
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
      path: "/login",
      query: { redirect: to.fullPath },
    };
  }

  if (to.meta?.guestOnly && isAuthenticated.value) {
    getCurrentUser();
    return "/";
  }

  return true;
});

export default router;
