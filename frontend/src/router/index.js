import { createRouter, createWebHistory } from "vue-router";
import { adminRoutes } from "./modules/adminRoutes";
import { customerRoutes } from "./modules/customerRoutes";

const router = createRouter({
  history: createWebHistory(),
  routes: [
    ...customerRoutes,
    ...adminRoutes,
    {
      path: "/:pathMatch(.*)*",
      redirect: "/",
    },
  ],
  scrollBehavior() {
    return { top: 0 };
  },
});

export default router;
