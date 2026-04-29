import CustomerLayout from "@/layouts/CustomerLayout.vue";
import AiRecommendView from "@/views/customer/AiRecommendView.vue";
import CartView from "@/views/customer/CartView.vue";
import FavoritesView from "@/views/customer/FavoritesView.vue";
import LoginView from "@/views/customer/LoginView.vue";
import MainView from "@/views/customer/MainView.vue";
import MenuDetailView from "@/views/customer/MenuDetailView.vue";
import MenuListView from "@/views/customer/MenuListView.vue";
import NotificationsView from "@/views/customer/NotificationsView.vue";
import OrderHistoryView from "@/views/customer/OrderHistoryView.vue";
import OrderStatusView from "@/views/customer/OrderStatusView.vue";
import ReviewWriteView from "@/views/customer/ReviewWriteView.vue";

export const customerRoutes = [
  {
    path: "/login",
    name: "customer-login",
    component: LoginView,
    meta: { title: "고객 로그인", guestOnly: true },
  },
  {
    path: "/",
    component: CustomerLayout,
    children: [
      {
        path: "",
        name: "customer-main",
        component: MainView,
        meta: { title: "메인 화면" },
      },
      {
        path: "menus",
        name: "menu-list",
        component: MenuListView,
        meta: { title: "메뉴 목록" },
      },
      {
        path: "menus/:menuId",
        name: "menu-detail",
        component: MenuDetailView,
        meta: { title: "메뉴 상세" },
      },
      {
        path: "cart",
        name: "cart",
        component: CartView,
        meta: { title: "장바구니" },
      },
      {
        path: "orders/status",
        name: "order-status",
        component: OrderStatusView,
        meta: { title: "주문 상태", requiresAuth: true },
      },
      {
        path: "orders/history",
        name: "order-history",
        component: OrderHistoryView,
        meta: { title: "주문 이력", requiresAuth: true },
      },
      {
        path: "ai/recommend",
        name: "ai-recommend",
        component: AiRecommendView,
        meta: { title: "AI 추천" },
      },
      {
        path: "reviews/write",
        name: "review-write",
        component: ReviewWriteView,
        meta: { title: "리뷰 작성", requiresAuth: true },
      },
      {
        path: "favorites",
        name: "favorites",
        component: FavoritesView,
        meta: { title: "즐겨찾기", requiresAuth: true },
      },
      {
        path: "notifications",
        name: "notifications",
        component: NotificationsView,
        meta: { title: "알림", requiresAuth: true },
      },
    ],
  },
];
