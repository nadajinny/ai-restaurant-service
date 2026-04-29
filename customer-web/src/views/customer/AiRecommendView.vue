<script setup>
import { aiApi, menuApi } from "@/api";
import PageHero from "@/components/PageHero.vue";
import PagePanel from "@/components/PagePanel.vue";
import { useCart } from "@/composables/useCart";
import { useCurrentUser } from "@/composables/useCurrentUser";
import { formatCurrency } from "@/utils/format";
import { computed, onMounted, ref } from "vue";

const { addItem, totalQuantity } = useCart();
const { ensureCurrentUser, getCurrentUser } = useCurrentUser();

const menuCatalog = ref([]);
const recommendationMessage = ref("오늘 먹고 싶은 메뉴를 자연어로 입력해 보세요.");
const keywordPrompt = ref("매운 음식이 먹고 싶어");
const emotion = ref("stressed");
const emotionContext = ref("");
const loading = ref(false);
const personalizedLoading = ref(false);
const recommendations = ref([]);
const personalizedRecommendations = ref([]);
const errorMessage = ref("");
const feedbackMessage = ref("");

const keywordOptions = [
  "매운 음식",
  "든든한 한 끼",
  "가벼운 점심",
  "국물 요리",
  "바삭한 메뉴",
  "달콤한 디저트",
];

const emotionOptions = [
  { label: "피곤해요", value: "tired" },
  { label: "스트레스가 많아요", value: "stressed" },
  { label: "기분 전환이 필요해요", value: "need-refresh" },
  { label: "행복한 기분이에요", value: "happy" },
];

const menuMap = computed(
  () => new Map(menuCatalog.value.map((menu) => [menu.menuId, menu])),
);

const recommendationCards = computed(() =>
  recommendations.value
    .map((item) => {
      const menu = menuMap.value.get(item.menuId);
      if (!menu) {
        return null;
      }

      return {
        ...menu,
        reason: item.reason,
      };
    })
    .filter(Boolean),
);

const personalizedCards = computed(() =>
  personalizedRecommendations.value
    .map((item) => {
      const menu = menuMap.value.get(item.menuId);
      if (!menu) {
        return null;
      }

      return {
        ...menu,
        reason: item.reason,
      };
    })
    .filter(Boolean),
);

async function loadMenuCatalog() {
  try {
    menuCatalog.value = await menuApi.getMenus();
  } catch {
    menuCatalog.value = [];
  }
}

async function requestRecommend(message) {
  loading.value = true;
  errorMessage.value = "";
  feedbackMessage.value = "";

  try {
    const response = await aiApi.recommend({ message });
    recommendations.value = response.recommendations ?? [];
    recommendationMessage.value = message;
    feedbackMessage.value = recommendations.value.length
      ? "AI 추천 결과를 불러왔습니다."
      : "추천 결과가 없어 기본 추천 안내를 표시합니다.";
  } catch (error) {
    recommendations.value = [];
    errorMessage.value =
      error.message ?? "AI 서버 오류로 추천을 불러오지 못했습니다. 잠시 후 다시 시도해 주세요.";
    feedbackMessage.value = "AI 서버 오류 시 기본 추천 또는 안내 메시지가 제공됩니다.";
  } finally {
    loading.value = false;
  }
}

async function requestEmotionRecommend() {
  loading.value = true;
  errorMessage.value = "";
  feedbackMessage.value = "";

  try {
    const response = await aiApi.emotionRecommend({
      emotion: emotion.value,
      context: emotionContext.value,
    });
    recommendations.value = response.recommendations ?? [];
    recommendationMessage.value = `${emotion.value} 감정 기반 추천`;
    feedbackMessage.value = recommendations.value.length
      ? "감정 기반 추천 결과를 불러왔습니다."
      : "추천 결과가 없어 기본 추천 안내를 표시합니다.";
  } catch (error) {
    recommendations.value = [];
    errorMessage.value =
      error.message ?? "감정 기반 추천을 불러오지 못했습니다.";
    feedbackMessage.value = "AI 서버 오류 시 기본 추천 또는 안내 메시지가 제공됩니다.";
  } finally {
    loading.value = false;
  }
}

async function loadPersonalizedRecommendations() {
  const currentUser = getCurrentUser();
  if (!currentUser?.id) {
    personalizedRecommendations.value = [];
    return;
  }

  personalizedLoading.value = true;

  try {
    const response = await aiApi.getPersonalizedRecommendations();
    personalizedRecommendations.value = response.recommendations ?? [];
  } catch {
    personalizedRecommendations.value = [];
  } finally {
    personalizedLoading.value = false;
  }
}

function applyKeyword(keyword) {
  keywordPrompt.value = `${keyword} 추천해줘`;
  requestRecommend(keywordPrompt.value);
}

function addRecommendationToCart(menu) {
  if (!menu.orderable) {
    feedbackMessage.value = "현재 주문할 수 없는 메뉴입니다.";
    return;
  }

  addItem(menu);
  feedbackMessage.value = `${menu.name}을(를) 장바구니에 담았습니다.`;
}

onMounted(async () => {
  await loadMenuCatalog();
  await Promise.all([
    requestRecommend(keywordPrompt.value),
    loadPersonalizedRecommendations(),
  ]);
});
</script>

<template>
  <div class="page-stack">
    <PageHero
      badge="AI"
      title="AI 추천 화면"
      description="자연어, 키워드, 감정 기반 추천을 요청하고 결과 메뉴를 바로 장바구니에 담을 수 있습니다."
    />

    <PagePanel
      title="자연어 추천"
      endpoint="POST /ai/recommend"
      description="원하는 음식 취향을 자연어로 입력하면 추천 이유와 함께 메뉴를 제안합니다."
    >
      <p v-if="feedbackMessage" class="info-banner">{{ feedbackMessage }}</p>
      <p v-if="errorMessage" class="error-banner">{{ errorMessage }}</p>

      <div class="ai-form-grid">
        <label class="field-stack ai-form-grid__wide">
          <span>자연어 입력</span>
          <textarea
            v-model="keywordPrompt"
            class="app-field review-textarea"
            rows="4"
            placeholder="예: 오늘 매운 국물 요리가 먹고 싶어"
          />
        </label>

        <div class="field-stack">
          <span>빠른 키워드</span>
          <div class="keyword-list">
            <button
              v-for="keyword in keywordOptions"
              :key="keyword"
              type="button"
              class="keyword-chip"
              @click="applyKeyword(keyword)"
            >
              {{ keyword }}
            </button>
          </div>
        </div>

        <div class="menu-card__actions">
          <button type="button" class="primary-button" :disabled="loading" @click="requestRecommend(keywordPrompt)">
            {{ loading ? "추천 요청 중" : "자연어 추천 요청" }}
          </button>
        </div>
      </div>
    </PagePanel>

    <PagePanel
      title="감정 기반 추천"
      endpoint="POST /ai/emotion-recommend"
      description="현재 감정과 상황을 입력해 분위기에 맞는 메뉴를 추천받습니다."
    >
      <div class="ai-form-grid">
        <label class="field-stack">
          <span>감정 선택</span>
          <select v-model="emotion" class="app-field">
            <option v-for="option in emotionOptions" :key="option.value" :value="option.value">
              {{ option.label }}
            </option>
          </select>
        </label>

        <label class="field-stack ai-form-grid__wide">
          <span>상황 설명</span>
          <input
            v-model="emotionContext"
            class="app-field"
            type="text"
            placeholder="예: 야근하고 집에 가는 길이야"
          />
        </label>

        <div class="menu-card__actions">
          <button type="button" class="secondary-button" :disabled="loading" @click="requestEmotionRecommend">
            {{ loading ? "추천 요청 중" : "감정 기반 추천 요청" }}
          </button>
        </div>
      </div>
    </PagePanel>

    <PagePanel
      title="추천 결과"
      description="추천 이유를 함께 확인하고 바로 장바구니에 담을 수 있습니다."
    >
      <div class="menu-toolbar__summary">
        <strong>현재 추천 기준</strong>
        <span>{{ recommendationMessage }}</span>
        <span>장바구니 수량 {{ totalQuantity }}개</span>
      </div>

      <p v-if="loading" class="state-copy">추천 결과를 불러오는 중입니다.</p>
      <p v-else-if="recommendationCards.length === 0" class="state-copy">
        추천 결과가 없습니다. 입력을 바꾸거나 잠시 후 다시 시도해 주세요.
      </p>

      <div v-else class="menu-grid">
        <article v-for="menu in recommendationCards" :key="`recommend-${menu.menuId}`" class="menu-card">
          <div class="menu-card__image-wrap">
            <img :src="menu.imageUrl" :alt="menu.name" class="menu-card__image" />
            <span class="menu-status" :data-status="menu.status">
              {{ menu.orderable ? "주문 가능" : "주문 불가" }}
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
            <p class="recommend-reason">{{ menu.reason }}</p>
            <div class="menu-card__actions">
              <router-link :to="`/menus/${menu.menuId}`" class="secondary-button">
                상세 보기
              </router-link>
              <button
                type="button"
                class="primary-button"
                :disabled="!menu.orderable"
                @click="addRecommendationToCart(menu)"
              >
                {{ menu.orderable ? "장바구니 추가" : "주문 불가" }}
              </button>
            </div>
          </div>
        </article>
      </div>
    </PagePanel>

    <PagePanel
      title="개인화 추천"
      endpoint="GET /ai/personalized-recommendations"
      description="주문 이력과 선호 기반 추천을 별도 섹션으로 제공합니다."
    >
      <p v-if="personalizedLoading" class="state-copy">개인화 추천을 불러오는 중입니다.</p>
      <p v-else-if="personalizedCards.length === 0" class="state-copy">
        개인화 추천 데이터가 아직 충분하지 않습니다.
      </p>

      <div v-else class="menu-grid">
        <article v-for="menu in personalizedCards" :key="`personalized-${menu.menuId}`" class="menu-card">
          <div class="menu-card__image-wrap">
            <img :src="menu.imageUrl" :alt="menu.name" class="menu-card__image" />
            <span class="menu-status" :data-status="menu.status">
              {{ menu.orderable ? "주문 가능" : "주문 불가" }}
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
            <p class="recommend-reason">{{ menu.reason }}</p>
            <div class="menu-card__actions">
              <router-link :to="`/menus/${menu.menuId}`" class="secondary-button">
                상세 보기
              </router-link>
              <button
                type="button"
                class="primary-button"
                :disabled="!menu.orderable"
                @click="addRecommendationToCart(menu)"
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
