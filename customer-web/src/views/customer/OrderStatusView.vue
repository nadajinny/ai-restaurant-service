<script setup>
import { orderApi } from "@/api";
import PageHero from "@/components/PageHero.vue";
import PagePanel from "@/components/PagePanel.vue";
import { useCurrentUser } from "@/composables/useCurrentUser";
import { formatCurrency } from "@/utils/format";
import { onMounted, ref, watch } from "vue";
import { useRoute } from "vue-router";

const route = useRoute();
const { ensureCurrentUser } = useCurrentUser();
const order = ref(null);
const loading = ref(false);
const errorMessage = ref("");

const statusSteps = ["RECEIVED", "COOKING", "READY", "COMPLETED"];

function resolveStatusLabel(status) {
  const labels = {
    RECEIVED: "주문 접수",
    COOKING: "조리 중",
    READY: "준비 완료",
    COMPLETED: "완료",
    CANCELED: "취소됨",
  };

  return labels[status] ?? status;
}

function isStepActive(status) {
  if (!order.value) {
    return false;
  }

  if (order.value.status === "CANCELED") {
    return false;
  }

  return statusSteps.indexOf(status) <= statusSteps.indexOf(order.value.status);
}

async function fetchOrder() {
  const orderId = route.query.orderId;

  if (!orderId) {
    errorMessage.value = "주문 상태를 확인할 주문 정보가 없습니다.";
    order.value = null;
    return;
  }

  loading.value = true;
  errorMessage.value = "";

  try {
    await ensureCurrentUser();
    order.value = await orderApi.getOrder(orderId);
  } catch (error) {
    errorMessage.value = error.message ?? "주문 상태를 조회하지 못했습니다.";
  } finally {
    loading.value = false;
  }
}

watch(() => route.query, fetchOrder, { deep: true });
onMounted(fetchOrder);
</script>

<template>
  <div class="page-stack">
    <PageHero
      badge="Order Tracking"
      title="주문 상태 화면"
      description="주문 생성 후 현재 상태와 주문 항목, 총 금액을 확인하는 고객용 화면입니다."
    />
    <PagePanel
      title="주문 상태"
      endpoint="GET /orders/{orderId}"
      description="주문 번호, 상태, 주문 항목, 총 금액을 확인합니다."
    >
      <p v-if="errorMessage" class="error-banner">{{ errorMessage }}</p>
      <p v-if="loading" class="state-copy">주문 상태를 불러오는 중입니다.</p>

      <template v-else-if="order">
        <div class="order-overview">
          <div class="order-overview__meta">
            <div>
              <span>주문 번호</span>
              <strong>#{{ order.orderId }}</strong>
            </div>
            <div>
              <span>주문 상태</span>
              <strong>{{ resolveStatusLabel(order.status) }}</strong>
            </div>
            <div>
              <span>총 금액</span>
              <strong>{{ formatCurrency(order.totalPrice) }}원</strong>
            </div>
          </div>

          <div v-if="order.status !== 'CANCELED'" class="status-timeline">
            <div
              v-for="step in statusSteps"
              :key="step"
              class="status-timeline__step"
              :data-active="isStepActive(step)"
            >
              {{ resolveStatusLabel(step) }}
            </div>
          </div>
        </div>

        <div class="order-items">
          <article v-for="item in order.items" :key="`${order.orderId}-${item.menuId}`" class="order-item-card">
            <div>
              <p class="menu-card__category">{{ item.menuName }}</p>
              <h3>{{ item.quantity }}개</h3>
            </div>
            <strong>{{ formatCurrency(item.itemPrice * item.quantity) }}원</strong>
          </article>
        </div>
      </template>
    </PagePanel>
  </div>
</template>
