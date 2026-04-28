import { createRouter, createWebHistory } from "vue-router";
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

export default router;
