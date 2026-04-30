<script setup>
import { menuApi } from "@/api";
import PageHero from "@/components/PageHero.vue";
import PagePanel from "@/components/PagePanel.vue";
import { useCart } from "@/composables/useCart";
import { formatCurrency } from "@/utils/format";
import { onMounted, ref, watch } from "vue";

const menus = ref([]);
const categoryOptions = ref([]);
const loading = ref(false);
const errorMessage = ref("");
const feedbackMessage = ref("");
const filters = ref({
  category: "",
  sort: "LATEST",
});

const { addItem, totalQuantity } = useCart();

const sortOptions = [
  { label: "최신순", value: "LATEST" },
  { label: "가격 낮은 순", value: "PRICE_ASC" },
  { label: "가격 높은 순", value: "PRICE_DESC" },
  { label: "인기순", value: "POPULAR" },
  { label: "평점순", value: "RATING" },
];

async function fetchMenus() {
  loading.value = true;
  errorMessage.value = "";

  try {
    const response = await menuApi.getMenus({
      category: filters.value.category || undefined,
      sort: filters.value.sort,
    });
    menus.value = response ?? [];
  } catch (error) {
    errorMessage.value = error.message ?? "메뉴를 불러오지 못했습니다.";
  } finally {
    loading.value = false;
  }
}

async function fetchCategoryOptions() {
  try {
    const response = await menuApi.getMenus();
    categoryOptions.value = [...new Set((response ?? []).map((menu) => menu.category))].sort();
  } catch {
    categoryOptions.value = [];
  }
}

function handleAddToCart(menu) {
  if (!menu.orderable) {
    feedbackMessage.value = "품절 또는 판매 중지 메뉴는 장바구니에 담을 수 없습니다.";
    return;
  }

  addItem(menu);
  feedbackMessage.value = `${menu.name}을(를) 장바구니에 담았습니다.`;
}

watch(
  filters,
  () => {
    fetchMenus();
  },
  { deep: true },
);

onMounted(async () => {
  await Promise.all([fetchMenus(), fetchCategoryOptions()]);
});
</script>

<template>
  <div class="page-stack">
    <PageHero
      badge="Menus"
      title="메뉴 목록 화면"
      description="카테고리와 정렬 조건으로 메뉴를 둘러보고 원하는 메뉴를 고를 수 있습니다."
    />

    <PagePanel title="탐색 조건" description="카테고리와 정렬 기준을 바꿔 원하는 메뉴를 빠르게 찾을 수 있습니다.">
      <div class="menu-toolbar">
        <label class="field-stack">
          <span>카테고리</span>
          <select v-model="filters.category" class="app-field">
            <option value="">전체</option>
            <option v-for="category in categoryOptions" :key="category" :value="category">
              {{ category }}
            </option>
          </select>
        </label>

        <label class="field-stack">
          <span>정렬</span>
          <select v-model="filters.sort" class="app-field">
            <option v-for="option in sortOptions" :key="option.value" :value="option.value">
              {{ option.label }}
            </option>
          </select>
        </label>

        <div class="menu-toolbar__summary">
          <strong>장바구니 수량</strong>
          <span>{{ totalQuantity }}개</span>
        </div>
      </div>
    </PagePanel>

    <PagePanel
      title="메뉴 목록"
      description="품절 메뉴는 확인할 수 있지만 주문은 할 수 없고, 판매 중지 메뉴는 목록에서 제외됩니다."
    >
      <p v-if="feedbackMessage" class="info-banner">{{ feedbackMessage }}</p>
      <p v-if="errorMessage" class="error-banner">{{ errorMessage }}</p>
      <p v-if="loading" class="state-copy">메뉴를 불러오는 중입니다.</p>
      <p v-else-if="menus.length === 0" class="state-copy">조건에 맞는 메뉴가 없습니다.</p>

      <div v-else class="menu-grid">
        <article v-for="menu in menus" :key="menu.menuId" class="menu-card">
          <div class="menu-card__image-wrap">
            <img :src="menu.imageUrl" :alt="menu.name" class="menu-card__image" />
            <span class="menu-status" :data-status="menu.status">
              {{ menu.orderable ? "주문 가능" : menu.status === "SOLD_OUT" ? "품절" : "판매 중지" }}
            </span>
          </div>
          <div class="menu-card__body">
            <div class="menu-card__header">
              <div>
                <p class="menu-card__category">{{ menu.category }}</p>
                <h3>{{ menu.name }}</h3>
              </div>
              <strong>{{ formatCurrency(menu.price) }}원</strong>
            </div>
            <p class="menu-card__meta">예상 조리 시간 {{ menu.cookingTime }}분</p>
            <div class="menu-card__actions">
              <router-link :to="`/menus/${menu.menuId}`" class="secondary-button">
                상세 보기
              </router-link>
              <button
                type="button"
                class="primary-button"
                :disabled="!menu.orderable"
                @click="handleAddToCart(menu)"
              >
                {{ menu.orderable ? "장바구니 추가" : "주문 불가" }}
              </button>
            </div>
          </div>
        </article>
      </div>
    </PagePanel>
  </div>
</template>
