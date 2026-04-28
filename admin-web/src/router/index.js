import { createRouter, createWebHistory } from "vue-router";
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

export default router;
