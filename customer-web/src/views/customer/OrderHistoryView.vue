<script setup>
import { orderApi } from "@/api";
import PageHero from "@/components/PageHero.vue";
import PagePanel from "@/components/PagePanel.vue";
import { useCurrentUser } from "@/composables/useCurrentUser";
import { formatCurrency } from "@/utils/format";
import { computed, onMounted, ref } from "vue";
import { useRouter } from "vue-router";

const router = useRouter();
const { ensureCurrentUser } = useCurrentUser();

const orders = ref([]);
const selectedOrder = ref(null);
const loading = ref(false);
const detailLoading = ref(false);
const reorderLoadingId = ref(null);
const errorMessage = ref("");
const reorderMessage = ref("");
const currentUserId = ref(null);

const emptyState = computed(() => !loading.value && orders.value.length === 0);

function formatDateTime(value) {
  if (!value) {
    return "-";
  }

  return new Intl.DateTimeFormat("ko-KR", {
    dateStyle: "medium",
    timeStyle: "short",
  }).format(new Date(value));
}

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

async function fetchOrders() {
  loading.value = true;
  errorMessage.value = "";

  try {
    const user = await ensureCurrentUser();
    currentUserId.value = user.id;
    orders.value = await orderApi.getOrders();
    if (orders.value.length > 0) {
      await openOrderDetail(orders.value[0].orderId);
    } else {
      selectedOrder.value = null;
    }
  } catch (error) {
    errorMessage.value = error.message ?? "주문 이력을 불러오지 못했습니다.";
  } finally {
    loading.value = false;
  }
}

async function openOrderDetail(orderId) {
  if (!currentUserId.value) {
    return;
  }

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

async function reorder(orderSummary) {
  if (!currentUserId.value) {
    return;
  }

  reorderLoadingId.value = orderSummary.orderId;
  reorderMessage.value = "";
  errorMessage.value = "";

  try {
    const response = await orderApi.reorder(orderSummary.orderId, {});
    const notices = [];

    if (response.totalPrice !== orderSummary.totalPrice) {
      notices.push(
        `현재 가격 기준으로 총액이 ${formatCurrency(response.totalPrice)}원으로 재계산되었습니다.`,
      );
    }

    if (response.unavailableItems?.length) {
      notices.push(
        `재주문 불가 메뉴: ${response.unavailableItems.map((item) => item.menuName).join(", ")}`,
      );
    }

    reorderMessage.value =
      notices.length > 0
        ? notices.join(" ")
        : "재주문이 완료되었습니다.";

    await fetchOrders();
    await router.push(`/orders/status?orderId=${response.orderId}`);
  } catch (error) {
    errorMessage.value = error.message ?? "재주문에 실패했습니다.";
  } finally {
    reorderLoadingId.value = null;
  }
}

onMounted(fetchOrders);
</script>

<template>
  <div class="page-stack">
    <PageHero
      badge="Order History"
      title="주문 이력 화면"
      description="최신순 주문 목록을 보고 상세를 열거나 현재 판매 가능 상태로 재주문할 수 있습니다."
    />
    <PagePanel
      title="주문 이력"
      endpoint="GET /orders"
      description="주문 목록은 최신순으로 표시하고, 상세 조회와 재주문을 같은 화면에서 처리합니다."
    >
      <p v-if="reorderMessage" class="info-banner">{{ reorderMessage }}</p>
      <p v-if="errorMessage" class="error-banner">{{ errorMessage }}</p>
      <p v-if="loading" class="state-copy">주문 이력을 불러오는 중입니다.</p>
      <p v-else-if="emptyState" class="state-copy">주문 이력이 없습니다.</p>

      <template v-else>
        <div class="history-grid">
          <div class="history-list">
            <article
              v-for="order in orders"
              :key="order.orderId"
              class="history-card"
              :data-selected="selectedOrder?.orderId === order.orderId"
            >
              <div class="history-card__top">
                <div>
                  <p class="menu-card__category">주문 #{{ order.orderId }}</p>
                  <h3>{{ order.representativeMenuName }}</h3>
                </div>
                <span class="history-card__status">{{ resolveStatusLabel(order.status) }}</span>
              </div>
              <p class="menu-card__meta">{{ formatDateTime(order.orderedAt) }}</p>
              <strong>{{ formatCurrency(order.totalPrice) }}원</strong>
              <div class="menu-card__actions">
                <button type="button" class="secondary-button" @click="openOrderDetail(order.orderId)">
                  상세 보기
                </button>
                <button
                  type="button"
                  class="primary-button"
                  :disabled="reorderLoadingId === order.orderId"
                  @click="reorder(order)"
                >
                  {{ reorderLoadingId === order.orderId ? "재주문 중" : "재주문" }}
                </button>
              </div>
            </article>
          </div>

          <div class="history-detail">
            <p v-if="detailLoading" class="state-copy">주문 상세를 불러오는 중입니다.</p>
            <p v-else-if="!selectedOrder" class="state-copy">좌측 주문을 선택하면 상세 정보를 확인할 수 있습니다.</p>

            <div v-else class="history-detail__panel">
              <div class="order-overview__meta">
                <div>
                  <span>주문 번호</span>
                  <strong>#{{ selectedOrder.orderId }}</strong>
                </div>
                <div>
                  <span>상태</span>
                  <strong>{{ resolveStatusLabel(selectedOrder.status) }}</strong>
                </div>
                <div>
                  <span>총 금액</span>
                  <strong>{{ formatCurrency(selectedOrder.totalPrice) }}원</strong>
                </div>
              </div>

              <div class="order-items">
                <article
                  v-for="item in selectedOrder.items"
                  :key="`${selectedOrder.orderId}-${item.menuId}`"
                  class="order-item-card"
                >
                  <div>
                    <p class="menu-card__category">{{ item.menuName }}</p>
                    <h3>{{ item.quantity }}개</h3>
                  </div>
                  <div class="order-item-card__actions">
                    <strong>{{ formatCurrency(item.itemPrice * item.quantity) }}원</strong>
                    <router-link
                      class="secondary-button"
                      :to="`/reviews/write?orderId=${selectedOrder.orderId}&menuId=${item.menuId}&menuName=${encodeURIComponent(item.menuName)}`"
                    >
                      리뷰 작성
                    </router-link>
                  </div>
                </article>
              </div>
            </div>
          </div>
        </div>
      </template>
    </PagePanel>
  </div>
</template>
