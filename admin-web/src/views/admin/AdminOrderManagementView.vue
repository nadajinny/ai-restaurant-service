<script setup>
import { orderApi } from "@/api";
import PageHero from "@/components/PageHero.vue";
import PagePanel from "@/components/PagePanel.vue";
import { formatCurrency, formatDateTime } from "@/utils/format";
import { computed, onMounted, ref } from "vue";

const orders = ref([]);
const selectedOrder = ref(null);
const loading = ref(false);
const detailLoading = ref(false);
const updatingStatus = ref(false);
const errorMessage = ref("");
const feedbackMessage = ref("");
const statusLabels = {
  RECEIVED: "접수",
  COOKING: "조리 중",
  READY: "준비 완료",
  COMPLETED: "완료",
  CANCELED: "취소",
};
const nextStatusMap = {
  RECEIVED: ["COOKING", "CANCELED"],
  COOKING: ["READY"],
  READY: ["COMPLETED"],
  COMPLETED: [],
  CANCELED: [],
};
const availableNextStatuses = computed(() => {
  const currentStatus = selectedOrder.value?.status;
  return currentStatus ? nextStatusMap[currentStatus] ?? [] : [];
});

function getStatusLabel(status) {
  return statusLabels[status] ?? status;
}

async function fetchOrders() {
  loading.value = true;
  errorMessage.value = "";

  try {
    orders.value = await orderApi.getOrders();
    if (orders.value.length === 0) {
      selectedOrder.value = null;
      return;
    }

    if (!selectedOrder.value) {
      await openOrderDetail(orders.value[0].orderId);
    }
  } catch (error) {
    errorMessage.value = error.message ?? "주문 목록을 불러오지 못했습니다.";
  } finally {
    loading.value = false;
  }
}

async function openOrderDetail(orderId) {
  detailLoading.value = true;
  errorMessage.value = "";
  try {
    selectedOrder.value = await orderApi.getOrder(orderId);
  } catch (error) {
    errorMessage.value = error.message ?? "주문 상세를 불러오지 못했습니다.";
  } finally {
    detailLoading.value = false;
  }
}

async function changeStatus(orderId, status) {
  updatingStatus.value = true;
  errorMessage.value = "";
  feedbackMessage.value = "";

  try {
    await orderApi.updateStatus(orderId, status);
    feedbackMessage.value = `주문 상태를 ${getStatusLabel(status)}로 변경했습니다.`;
    await fetchOrders();
    await openOrderDetail(orderId);
  } catch (error) {
    errorMessage.value = error.message ?? "주문 상태 변경에 실패했습니다.";
  } finally {
    updatingStatus.value = false;
  }
}

onMounted(fetchOrders);
</script>

<template>
  <div class="page-stack">
    <PageHero badge="Admin Orders" title="주문 관리" description="주문 목록을 조회하고 상태를 변경합니다." />
    <PagePanel title="주문 목록 및 상세">
      <p v-if="feedbackMessage" class="info-banner">{{ feedbackMessage }}</p>
      <p v-if="errorMessage" class="error-banner">{{ errorMessage }}</p>

      <div class="admin-grid admin-grid--two">
        <div class="data-card">
          <h3>주문 목록</h3>
          <p v-if="loading" class="state-copy">주문 목록을 불러오는 중입니다.</p>
          <ul v-else class="stack-list">
            <li v-for="order in orders" :key="order.orderId">
              <button type="button" class="list-button" @click="openOrderDetail(order.orderId)">
                <strong>#{{ order.orderId }} · {{ order.representativeMenuName }}</strong>
                <span>{{ formatDateTime(order.orderedAt) }}</span>
                <span>{{ getStatusLabel(order.status) }} · {{ formatCurrency(order.totalPrice) }}원</span>
              </button>
            </li>
          </ul>
        </div>

        <div class="data-card">
          <h3>주문 상세</h3>
          <p v-if="detailLoading" class="state-copy">주문 상세를 불러오는 중입니다.</p>
          <p v-else-if="!selectedOrder" class="state-copy">좌측 주문을 선택하세요.</p>
          <template v-else>
            <div class="metric-grid">
              <article class="metric-card">
                <span>주문 번호</span>
                <strong>#{{ selectedOrder.orderId }}</strong>
              </article>
              <article class="metric-card">
                <span>주문 상태</span>
                <strong>{{ getStatusLabel(selectedOrder.status) }}</strong>
              </article>
              <article class="metric-card">
                <span>총 금액</span>
                <strong>{{ formatCurrency(selectedOrder.totalPrice) }}원</strong>
              </article>
            </div>

            <div class="admin-actions">
              <button
                v-for="status in availableNextStatuses"
                :key="status"
                type="button"
                class="tiny-button"
                :disabled="updatingStatus"
                @click="changeStatus(selectedOrder.orderId, status)"
              >
                {{ updatingStatus ? "변경 중" : `${getStatusLabel(status)}로 변경` }}
              </button>
            </div>
            <p v-if="availableNextStatuses.length === 0" class="state-copy">
              현재 상태에서는 변경 가능한 다음 상태가 없습니다.
            </p>

            <div class="table-wrap">
              <table class="admin-table">
                <thead>
                  <tr>
                    <th>메뉴</th>
                    <th>수량</th>
                    <th>주문 당시 가격</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="item in selectedOrder.items" :key="`${selectedOrder.orderId}-${item.menuId}`">
                    <td>{{ item.menuName }}</td>
                    <td>{{ item.quantity }}</td>
                    <td>{{ formatCurrency(item.itemPrice) }}원</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </template>
        </div>
      </div>
    </PagePanel>
  </div>
</template>
