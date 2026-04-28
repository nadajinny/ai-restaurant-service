import AdminLayout from "@/layouts/AdminLayout.vue";
import AdminAiMenuIdeasView from "@/views/admin/AdminAiMenuIdeasView.vue";
import AdminCouponManagementView from "@/views/admin/AdminCouponManagementView.vue";
import AdminDashboardView from "@/views/admin/AdminDashboardView.vue";
import AdminInventoryManagementView from "@/views/admin/AdminInventoryManagementView.vue";
import AdminLoginView from "@/views/admin/AdminLoginView.vue";
import AdminMenuManagementView from "@/views/admin/AdminMenuManagementView.vue";
import AdminOrderManagementView from "@/views/admin/AdminOrderManagementView.vue";
import AdminReviewManagementView from "@/views/admin/AdminReviewManagementView.vue";
import AdminSalesManagementView from "@/views/admin/AdminSalesManagementView.vue";

export const adminRoutes = [
  {
    path: "/admin/login",
    name: "admin-login",
    component: AdminLoginView,
    meta: { title: "관리자 로그인", guestOnly: true },
  },
  {
    path: "/admin",
    component: AdminLayout,
    meta: { requiresAuth: true, requiredRole: "ADMIN" },
    children: [
      {
        path: "",
        name: "admin-dashboard",
        component: AdminDashboardView,
        meta: { title: "관리자 대시보드" },
      },
      {
        path: "menus",
        name: "admin-menus",
        component: AdminMenuManagementView,
        meta: { title: "메뉴 관리" },
      },
      {
        path: "orders",
        name: "admin-orders",
        component: AdminOrderManagementView,
        meta: { title: "주문 관리" },
      },
      {
        path: "reviews",
        name: "admin-reviews",
        component: AdminReviewManagementView,
        meta: { title: "리뷰 관리" },
      },
      {
        path: "inventories",
        name: "admin-inventories",
        component: AdminInventoryManagementView,
        meta: { title: "재고 관리" },
      },
      {
        path: "sales",
        name: "admin-sales",
        component: AdminSalesManagementView,
        meta: { title: "매출 관리" },
      },
      {
        path: "coupons",
        name: "admin-coupons",
        component: AdminCouponManagementView,
        meta: { title: "쿠폰 관리" },
      },
      {
        path: "ai/new-menus",
        name: "admin-ai-new-menus",
        component: AdminAiMenuIdeasView,
        meta: { title: "AI 신메뉴 추천" },
      },
    ],
  },
];
