<script setup>
import { adminAnalyticsApi } from "@/api";
import PageHero from "@/components/PageHero.vue";
import PagePanel from "@/components/PagePanel.vue";
import { formatCurrency } from "@/utils/format";
import { onMounted, reactive, ref } from "vue";

const loading = ref(false);
const errorMessage = ref("");
const sales = ref(null);
const popularMenus = ref([]);
const menuPerformance = ref([]);
const hourlyOrders = ref([]);

const filters = reactive({
  startDate: "",
  endDate: "",
});

async function fetchAnalytics() {
  loading.value = true;
  errorMessage.value = "";

  try {
    const [salesData, popularData, performanceData, hourlyData] = await Promise.all([
      adminAnalyticsApi.getSalesAnalytics({
        startDate: filters.startDate || undefined,
        endDate: filters.endDate || undefined,
      }),
      adminAnalyticsApi.getPopularMenus(),
      adminAnalyticsApi.getMenuPerformance(),
      adminAnalyticsApi.getHourlyOrders(),
    ]);

    sales.value = salesData;
    popularMenus.value = popularData;
    menuPerformance.value = performanceData;
    hourlyOrders.value = hourlyData;
  } catch (error) {
    errorMessage.value = error.message ?? "매출 데이터를 불러오지 못했습니다.";
  } finally {
    loading.value = false;
  }
}

onMounted(fetchAnalytics);
</script>

<template>
  <div class="page-stack">
    <PageHero badge="Admin Sales" title="매출 관리" description="기간별 매출과 메뉴 성과, 인기 메뉴, 시간대별 주문량을 확인합니다." />
    <PagePanel title="기간별 매출" endpoint="GET /admin/analytics/sales">
      <p v-if="errorMessage" class="error-banner">{{ errorMessage }}</p>

      <div class="admin-actions">
        <label class="field-stack">
          <span>시작일</span>
          <input v-model="filters.startDate" class="app-field" type="date" />
        </label>
        <label class="field-stack">
          <span>종료일</span>
          <input v-model="filters.endDate" class="app-field" type="date" />
        </label>
        <button type="button" class="primary-button" :disabled="loading" @click="fetchAnalytics">
          {{ loading ? "조회 중" : "기간 조회" }}
        </button>
      </div>

      <div v-if="sales" class="metric-grid">
        <article class="metric-card">
          <span>완료 주문 수</span>
          <strong>{{ sales.completedOrderCount }}건</strong>
        </article>
        <article class="metric-card">
          <span>총 매출</span>
          <strong>{{ formatCurrency(sales.totalSales) }}원</strong>
        </article>
        <article class="metric-card">
          <span>평균 주문 금액</span>
          <strong>{{ formatCurrency(sales.averageOrderAmount) }}원</strong>
        </article>
      </div>
    </PagePanel>

    <PagePanel title="성과 분석" endpoint="GET /admin/analytics/popular-menus, GET /admin/analytics/menu-performance, GET /admin/analytics/hourly-orders">
      <div class="admin-grid admin-grid--three">
        <section class="data-card">
          <h3>인기 메뉴</h3>
          <ul class="stack-list">
            <li v-for="menu in popularMenus" :key="menu.menuId">
              <strong>{{ menu.menuName }}</strong>
              <span>{{ menu.soldQuantity }}개 · {{ formatCurrency(menu.salesAmount) }}원</span>
            </li>
          </ul>
        </section>

        <section class="data-card">
          <h3>메뉴 성과</h3>
          <ul class="stack-list">
            <li v-for="menu in menuPerformance" :key="menu.menuId">
              <strong>{{ menu.menuName }}</strong>
              <span>{{ menu.category }} · {{ menu.soldQuantity }}개 · {{ formatCurrency(menu.salesAmount) }}원</span>
            </li>
          </ul>
        </section>

        <section class="data-card">
          <h3>시간대별 주문량</h3>
          <ul class="stack-list">
            <li v-for="hour in hourlyOrders" :key="hour.hour">
              <strong>{{ hour.hour }}시</strong>
              <span>{{ hour.orderCount }}건</span>
            </li>
          </ul>
        </section>
      </div>
    </PagePanel>
  </div>
</template>
