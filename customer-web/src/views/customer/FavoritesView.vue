<script setup>
import { favoriteApi } from "@/api";
import PageHero from "@/components/PageHero.vue";
import PagePanel from "@/components/PagePanel.vue";
import { useCart } from "@/composables/useCart";
import { useCurrentUser } from "@/composables/useCurrentUser";
import { formatCurrency } from "@/utils/format";
import { onMounted, ref } from "vue";

const { addItem } = useCart();
const { ensureCurrentUser } = useCurrentUser();

const favorites = ref([]);
const loading = ref(false);
const errorMessage = ref("");
const feedbackMessage = ref("");
const currentUserId = ref(null);

async function fetchFavorites() {
  loading.value = true;
  errorMessage.value = "";

  try {
    const user = await ensureCurrentUser();
    currentUserId.value = user.id;
    favorites.value = await favoriteApi.getFavorites();
  } catch (error) {
    errorMessage.value = error.message ?? "즐겨찾기 목록을 불러오지 못했습니다.";
  } finally {
    loading.value = false;
  }
}

function toCartItem(favorite) {
  return {
    menuId: favorite.menuId,
    name: favorite.name,
    price: favorite.price,
    category: favorite.category,
    imageUrl: favorite.imageUrl,
    description: "",
    cookingTime: 0,
    status: favorite.status,
    orderable: favorite.status === "AVAILABLE",
  };
}

function addFavoriteToCart(favorite) {
  if (favorite.status !== "AVAILABLE") {
    feedbackMessage.value = "현재 주문할 수 없는 메뉴입니다.";
    return;
  }

  addItem(toCartItem(favorite));
  feedbackMessage.value = `${favorite.name}을(를) 장바구니에 담았습니다.`;
}

async function removeFavorite(menuId) {
  if (!currentUserId.value) {
    return;
  }

  try {
    await favoriteApi.deleteFavorite(menuId);
    favorites.value = favorites.value.filter((item) => item.menuId !== menuId);
    feedbackMessage.value = "즐겨찾기를 해제했습니다.";
  } catch (error) {
    errorMessage.value = error.message ?? "즐겨찾기 해제에 실패했습니다.";
  }
}

onMounted(fetchFavorites);
</script>

<template>
  <div class="page-stack">
    <PageHero
      badge="Favorites"
      title="즐겨찾기 화면"
      description="저장한 메뉴를 다시 보거나 장바구니로 보내고 즐겨찾기를 해제할 수 있습니다."
    />
    <PagePanel
      title="즐겨찾기 목록"
      description="즐겨찾기한 메뉴의 판매 상태를 함께 표시합니다."
    >
      <p v-if="feedbackMessage" class="info-banner">{{ feedbackMessage }}</p>
      <p v-if="errorMessage" class="error-banner">{{ errorMessage }}</p>
      <p v-if="loading" class="state-copy">즐겨찾기 목록을 불러오는 중입니다.</p>
      <p v-else-if="favorites.length === 0" class="state-copy">아직 즐겨찾기한 메뉴가 없습니다.</p>

      <div v-else class="menu-grid">
        <article v-for="favorite in favorites" :key="favorite.menuId" class="menu-card">
          <div class="menu-card__image-wrap">
            <img :src="favorite.imageUrl" :alt="favorite.name" class="menu-card__image" />
            <span class="menu-status" :data-status="favorite.status">
              {{ favorite.status === "AVAILABLE" ? "주문 가능" : favorite.status === "SOLD_OUT" ? "품절" : "판매 중지" }}
            </span>
          </div>
          <div class="menu-card__body">
            <div class="menu-card__header">
              <div>
                <p class="menu-card__category">{{ favorite.category }}</p>
                <h3>{{ favorite.name }}</h3>
              </div>
              <strong>{{ formatCurrency(favorite.price) }}원</strong>
            </div>

            <div class="menu-card__actions">
              <router-link :to="`/menus/${favorite.menuId}`" class="secondary-button">
                상세 보기
              </router-link>
              <button type="button" class="ghost-button" @click="removeFavorite(favorite.menuId)">
                즐겨찾기 해제
              </button>
              <button
                type="button"
                class="primary-button"
                :disabled="favorite.status !== 'AVAILABLE'"
                @click="addFavoriteToCart(favorite)"
              >
                {{ favorite.status === "AVAILABLE" ? "장바구니 추가" : "주문 불가" }}
              </button>
            </div>
          </div>
        </article>
      </div>
    </PagePanel>
  </div>
</template>
