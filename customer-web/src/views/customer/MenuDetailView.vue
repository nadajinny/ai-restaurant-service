<script setup>
import { menuApi } from "@/api";
import PageHero from "@/components/PageHero.vue";
import PagePanel from "@/components/PagePanel.vue";
import { useCart } from "@/composables/useCart";
import { formatCurrency } from "@/utils/format";
import { onMounted, ref, watch } from "vue";
import { useRoute } from "vue-router";

const route = useRoute();
const { addItem, totalQuantity } = useCart();

const menu = ref(null);
const loading = ref(false);
const errorMessage = ref("");
const feedbackMessage = ref("");

async function fetchMenu() {
  loading.value = true;
  errorMessage.value = "";

  try {
    menu.value = await menuApi.getMenu(route.params.menuId);
  } catch (error) {
    errorMessage.value = error.message ?? "메뉴 상세 정보를 불러오지 못했습니다.";
  } finally {
    loading.value = false;
  }
}

function handleAddToCart() {
  if (!menu.value?.orderable) {
    feedbackMessage.value = "현재 주문할 수 없는 메뉴입니다.";
    return;
  }

  addItem(menu.value);
  feedbackMessage.value = `${menu.value.name}을(를) 장바구니에 담았습니다.`;
}

watch(
  () => route.params.menuId,
  () => {
    fetchMenu();
  },
);

onMounted(fetchMenu);
</script>

<template>
  <div class="page-stack">
    <PageHero
      badge="Menu Detail"
      title="메뉴 상세 화면"
      description="이름, 가격, 설명, 조리 시간과 판매 상태를 확인하고 장바구니에 담을 수 있습니다."
    />

    <PagePanel title="메뉴 상세" endpoint="GET /menus/{menuId}" description="판매 중지 메뉴는 조회되지 않고, 품절 메뉴는 주문 불가로 표시됩니다.">
      <p v-if="feedbackMessage" class="info-banner">{{ feedbackMessage }}</p>
      <p v-if="errorMessage" class="error-banner">{{ errorMessage }}</p>
      <p v-if="loading" class="state-copy">상세 정보를 불러오는 중입니다.</p>

      <div v-else-if="menu" class="menu-detail">
        <div class="menu-detail__image-panel">
          <img :src="menu.imageUrl" :alt="menu.name" class="menu-detail__image" />
        </div>
        <div class="menu-detail__content">
          <div class="menu-detail__topline">
            <span class="menu-chip">{{ menu.category }}</span>
            <span class="menu-status" :data-status="menu.status">
              {{ menu.orderable ? "주문 가능" : menu.status === "SOLD_OUT" ? "품절" : "판매 중지" }}
            </span>
          </div>
          <h2 class="menu-detail__title">{{ menu.name }}</h2>
          <p class="menu-detail__price">{{ formatCurrency(menu.price) }}원</p>
          <p class="menu-detail__description">{{ menu.description }}</p>

          <div class="detail-facts">
            <div class="detail-facts__item">
              <span>예상 조리 시간</span>
              <strong>{{ menu.cookingTime }}분</strong>
            </div>
            <div class="detail-facts__item">
              <span>장바구니 수량</span>
              <strong>{{ totalQuantity }}개</strong>
            </div>
          </div>

          <div class="menu-card__actions">
            <router-link to="/menus" class="secondary-button">목록으로</router-link>
            <button
              type="button"
              class="primary-button"
              :disabled="!menu.orderable"
              @click="handleAddToCart"
            >
              {{ menu.orderable ? "장바구니 추가" : "주문 불가" }}
            </button>
          </div>
        </div>
      </div>

      <p v-else class="state-copy">메뉴 정보를 찾을 수 없습니다.</p>
    </PagePanel>
  </div>
</template>
