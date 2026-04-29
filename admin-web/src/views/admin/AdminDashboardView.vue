<script setup>
import { adminAnalyticsApi } from "@/api";
import PageHero from "@/components/PageHero.vue";
import PagePanel from "@/components/PagePanel.vue";
import { formatCurrency, formatDateTime } from "@/utils/format";
import { onMounted, ref } from "vue";

const dashboard = ref(null);
const loading = ref(false);
const errorMessage = ref("");

async function fetchDashboard() {
  loading.value = true;
  errorMessage.value = "";

  try {
    dashboard.value = await adminAnalyticsApi.getDashboard();
  } catch (error) {
    errorMessage.value = error.message ?? "대시보드 데이터를 불러오지 못했습니다.";
  } finally {
    loading.value = false;
  }
}

onMounted(fetchDashboard);
</script>

<template>
  <div class="page-stack">
    <PageHero
      badge="Dashboard"
      title="관리자 대시보드"
      description="당일 주문 수, 당일 매출, 인기 메뉴, 시간대별 주문량, 품절 메뉴, 최근 리뷰를 한 화면에서 확인합니다."
    />

    <PagePanel title="운영 요약" endpoint="GET /admin/dashboard">
      <p v-if="errorMessage" class="error-banner">{{ errorMessage }}</p>
      <p v-if="loading" class="state-copy">대시보드를 불러오는 중입니다.</p>

      <template v-else-if="dashboard">
        <div class="metric-grid">
          <article class="metric-card">
            <span>당일 주문 수</span>
            <strong>{{ dashboard.todayOrderCount }}건</strong>
          </article>
          <article class="metric-card">
            <span>당일 매출</span>
            <strong>{{ formatCurrency(dashboard.todaySales) }}원</strong>
          </article>
          <article class="metric-card">
            <span>인기 메뉴 수</span>
            <strong>{{ dashboard.popularMenus.length }}개</strong>
          </article>
          <article class="metric-card">
            <span>품절 메뉴 수</span>
            <strong>{{ dashboard.soldOutMenus.length }}개</strong>
          </article>
        </div>

        <div class="admin-grid admin-grid--two">
          <section class="data-card">
            <h3>인기 메뉴</h3>
            <ul class="stack-list">
              <li v-for="menu in dashboard.popularMenus.slice(0, 5)" :key="menu.menuId">
                <strong>{{ menu.menuName }}</strong>
                <span>{{ menu.soldQuantity }}개 · {{ formatCurrency(menu.salesAmount) }}원</span>
              </li>
            </ul>
          </section>

          <section class="data-card">
            <h3>시간대별 주문량</h3>
            <ul class="stack-list">
              <li v-for="hourly in dashboard.hourlyOrders.slice(0, 8)" :key="hourly.hour">
                <strong>{{ hourly.hour }}시</strong>
                <span>{{ hourly.orderCount }}건</span>
              </li>
            </ul>
          </section>

          <section class="data-card">
            <h3>품절 메뉴</h3>
            <ul class="stack-list">
              <li v-if="dashboard.soldOutMenus.length === 0">품절 메뉴가 없습니다.</li>
              <li v-for="menu in dashboard.soldOutMenus" :key="menu.menuId">
                <strong>{{ menu.menuName }}</strong>
                <span>{{ menu.category }}</span>
              </li>
            </ul>
          </section>

          <section class="data-card">
            <h3>최근 리뷰</h3>
            <ul class="stack-list">
              <li v-if="dashboard.recentReviews.length === 0">최근 리뷰가 없습니다.</li>
              <li v-for="review in dashboard.recentReviews" :key="review.reviewId">
                <strong>{{ review.menuName }} · {{ review.rating }}점</strong>
                <span>{{ review.content }}</span>
                <small>{{ formatDateTime(review.createdAt) }}</small>
              </li>
            </ul>
          </section>
        </div>
      </template>
    </PagePanel>
  </div>
</template>
