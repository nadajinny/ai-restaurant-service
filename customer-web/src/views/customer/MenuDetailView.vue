<script setup>
import { favoriteApi, menuApi, reviewApi } from "@/api";
import PageHero from "@/components/PageHero.vue";
import PagePanel from "@/components/PagePanel.vue";
import { useCurrentUser } from "@/composables/useCurrentUser";
import { useCart } from "@/composables/useCart";
import { formatCurrency } from "@/utils/format";
import { computed, onMounted, ref, watch } from "vue";
import { useRoute } from "vue-router";

const route = useRoute();
const { ensureCurrentUser, getCurrentUser } = useCurrentUser();
const { addItem, totalQuantity } = useCart();

const menu = ref(null);
const reviews = ref([]);
const favoriteMenuIds = ref([]);
const loading = ref(false);
const reviewLoading = ref(false);
const favoriteLoading = ref(false);
const errorMessage = ref("");
const feedbackMessage = ref("");

const isFavorite = computed(() =>
  menu.value ? favoriteMenuIds.value.includes(menu.value.menuId) : false,
);

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

async function fetchReviews() {
  reviewLoading.value = true;

  try {
    reviews.value = await reviewApi.getMenuReviews(route.params.menuId);
  } catch {
    reviews.value = [];
  } finally {
    reviewLoading.value = false;
  }
}

async function fetchFavorites() {
  const currentUser = getCurrentUser();
  if (!currentUser?.id) {
    favoriteMenuIds.value = [];
    return;
  }

  favoriteLoading.value = true;

  try {
    const favorites = await favoriteApi.getFavorites();
    favoriteMenuIds.value = favorites.map((item) => item.menuId);
  } catch {
    favoriteMenuIds.value = [];
  } finally {
    favoriteLoading.value = false;
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

async function toggleFavorite() {
  if (!menu.value) {
    return;
  }

  favoriteLoading.value = true;
  feedbackMessage.value = "";

  try {
    await ensureCurrentUser();

    if (isFavorite.value) {
      await favoriteApi.deleteFavorite(menu.value.menuId);
      favoriteMenuIds.value = favoriteMenuIds.value.filter((id) => id !== menu.value.menuId);
      feedbackMessage.value = "즐겨찾기를 해제했습니다.";
    } else {
      await favoriteApi.createFavorite(menu.value.menuId);
      favoriteMenuIds.value = [...favoriteMenuIds.value, menu.value.menuId];
      feedbackMessage.value = "즐겨찾기에 추가했습니다.";
    }
  } catch (error) {
    feedbackMessage.value = error.message ?? "즐겨찾기 처리에 실패했습니다.";
  } finally {
    favoriteLoading.value = false;
  }
}

function renderStars(rating) {
  return "★".repeat(rating) + "☆".repeat(5 - rating);
}

watch(
  () => route.params.menuId,
  () => {
    fetchMenu();
    fetchReviews();
    fetchFavorites();
  },
);

onMounted(() => {
  fetchMenu();
  fetchReviews();
  fetchFavorites();
});
</script>

<template>
  <div class="page-stack">
    <PageHero
      badge="Menu Detail"
      title="메뉴 상세 화면"
      description="이름, 가격, 설명, 조리 시간과 판매 상태를 확인하고 장바구니에 담을 수 있습니다."
    />

    <PagePanel title="메뉴 상세" description="판매 중지 메뉴는 보이지 않으며, 품절 메뉴는 주문할 수 없도록 표시됩니다.">
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
              class="ghost-button"
              :disabled="favoriteLoading"
              @click="toggleFavorite"
            >
              {{ isFavorite ? "즐겨찾기 해제" : "즐겨찾기 추가" }}
            </button>
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

    <PagePanel
      title="리뷰 목록"
      description="다른 고객이 남긴 최신 리뷰를 확인할 수 있습니다."
    >
      <p v-if="reviewLoading" class="state-copy">리뷰를 불러오는 중입니다.</p>
      <p v-else-if="reviews.length === 0" class="state-copy">아직 작성된 리뷰가 없습니다.</p>

      <div v-else class="review-list">
        <article v-for="review in reviews" :key="review.reviewId" class="review-card">
          <div class="review-card__header">
            <strong>{{ renderStars(review.rating) }}</strong>
            <span class="review-card__meta">
              {{ new Date(review.createdAt).toLocaleDateString("ko-KR") }}
            </span>
          </div>
          <p class="review-card__content">{{ review.content }}</p>
          <span v-if="review.aiGenerated" class="review-badge">AI 초안 사용</span>
        </article>
      </div>
    </PagePanel>
  </div>
</template>
