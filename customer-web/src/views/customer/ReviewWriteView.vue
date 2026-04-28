<script setup>
import { aiApi, reviewApi } from "@/api";
import PageHero from "@/components/PageHero.vue";
import PagePanel from "@/components/PagePanel.vue";
import { useCurrentUser } from "@/composables/useCurrentUser";
import { computed, ref } from "vue";
import { useRoute, useRouter } from "vue-router";

const route = useRoute();
const router = useRouter();
const { ensureCurrentUser } = useCurrentUser();

const rating = ref(5);
const content = ref("");
const aiGenerated = ref(false);
const loading = ref(false);
const aiLoading = ref(false);
const feedbackMessage = ref("");
const errorMessage = ref("");

const keywordOptions = [
  "맛있다",
  "양이 많다",
  "친절하다",
  "재료가 신선하다",
  "가성비가 좋다",
  "재주문하고 싶다",
];

const selectedKeywords = ref(["맛있다", "친절하다"]);

const orderId = computed(() => route.query.orderId);
const menuId = computed(() => route.query.menuId);
const menuName = computed(() =>
  route.query.menuName ? decodeURIComponent(route.query.menuName) : "선택한 메뉴",
);
const canGenerateAiDraft = computed(() => selectedKeywords.value.length > 0);

function toggleKeyword(keyword) {
  if (selectedKeywords.value.includes(keyword)) {
    selectedKeywords.value = selectedKeywords.value.filter((item) => item !== keyword);
    return;
  }

  selectedKeywords.value = [...selectedKeywords.value, keyword];
}

async function generateAiDraft() {
  if (!menuId.value) {
    errorMessage.value = "리뷰를 생성할 메뉴 정보가 없습니다.";
    return;
  }

  aiLoading.value = true;
  errorMessage.value = "";

  try {
    const response = await aiApi.generateReview({
      menuId: Number(menuId.value),
      keywords: selectedKeywords.value,
    });
    content.value = response.reviewDraft;
    aiGenerated.value = true;
    feedbackMessage.value = "AI 후기 초안을 불러왔습니다. 저장 전에 수정할 수 있습니다.";
  } catch (error) {
    errorMessage.value = error.message ?? "AI 후기 생성에 실패했습니다.";
  } finally {
    aiLoading.value = false;
  }
}

async function submitReview() {
  if (!orderId.value || !menuId.value) {
    errorMessage.value = "리뷰를 작성할 주문 또는 메뉴 정보가 없습니다.";
    return;
  }

  loading.value = true;
  errorMessage.value = "";

  try {
    const user = await ensureCurrentUser();
    await reviewApi.createReview(user.id, {
      orderId: Number(orderId.value),
      menuId: Number(menuId.value),
      content: content.value,
      rating: rating.value,
      aiGenerated: aiGenerated.value,
    });

    await router.push("/orders/history");
  } catch (error) {
    errorMessage.value = error.message ?? "리뷰 저장에 실패했습니다.";
  } finally {
    loading.value = false;
  }
}
</script>

<template>
  <div class="page-stack">
    <PageHero
      badge="Review"
      title="리뷰 작성 화면"
      :description="`${menuName}에 대한 별점과 후기를 작성합니다.`"
    />
    <PagePanel
      title="리뷰 작성"
      endpoint="POST /reviews, POST /ai/review-generate"
      description="별점 입력, AI 후기 생성, 최종 저장을 한 화면에서 처리합니다."
    >
      <p v-if="feedbackMessage" class="info-banner">{{ feedbackMessage }}</p>
      <p v-if="errorMessage" class="error-banner">{{ errorMessage }}</p>

      <div class="review-form">
        <div class="field-stack">
          <span>메뉴</span>
          <strong>{{ menuName }}</strong>
        </div>

        <div class="field-stack">
          <span>별점</span>
          <div class="rating-row">
            <button
              v-for="star in 5"
              :key="star"
              type="button"
              class="star-button"
              :data-active="star <= rating"
              @click="rating = star"
            >
              ★
            </button>
          </div>
        </div>

        <div class="field-stack">
          <span>AI 후기 키워드</span>
          <div class="keyword-list">
            <button
              v-for="keyword in keywordOptions"
              :key="keyword"
              type="button"
              class="keyword-chip"
              :data-active="selectedKeywords.includes(keyword)"
              @click="toggleKeyword(keyword)"
            >
              {{ keyword }}
            </button>
          </div>
        </div>

        <div class="field-stack">
          <span>리뷰 내용</span>
          <textarea
            v-model="content"
            class="app-field review-textarea"
            rows="6"
            placeholder="주문한 메뉴에 대한 후기를 입력하세요."
          />
        </div>

        <div class="menu-card__actions">
          <button
            type="button"
            class="secondary-button"
            :disabled="aiLoading || !canGenerateAiDraft"
            @click="generateAiDraft"
          >
            {{ aiLoading ? "AI 생성 중" : "AI 후기 생성" }}
          </button>
          <button type="button" class="primary-button" :disabled="loading" @click="submitReview">
            {{ loading ? "저장 중" : "리뷰 저장" }}
          </button>
        </div>
      </div>
    </PagePanel>
  </div>
</template>
