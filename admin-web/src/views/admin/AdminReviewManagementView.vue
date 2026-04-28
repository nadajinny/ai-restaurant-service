<script setup>
import { reviewApi } from "@/api";
import PageHero from "@/components/PageHero.vue";
import PagePanel from "@/components/PagePanel.vue";
import { formatDateTime } from "@/utils/format";
import { onMounted, ref } from "vue";

const reviews = ref([]);
const loading = ref(false);
const errorMessage = ref("");
const feedbackMessage = ref("");

async function fetchReviews() {
  loading.value = true;
  errorMessage.value = "";

  try {
    reviews.value = await reviewApi.getAdminReviews();
  } catch (error) {
    errorMessage.value = error.message ?? "리뷰 목록을 불러오지 못했습니다.";
  } finally {
    loading.value = false;
  }
}

async function hideReview(reviewId) {
  try {
    await reviewApi.hideReview(reviewId);
    feedbackMessage.value = "리뷰를 숨김 처리했습니다.";
    await fetchReviews();
  } catch (error) {
    errorMessage.value = error.message ?? "리뷰 숨김 처리에 실패했습니다.";
  }
}

onMounted(fetchReviews);
</script>

<template>
  <div class="page-stack">
    <PageHero badge="Admin Reviews" title="리뷰 관리" description="리뷰 목록을 조회하고 부적절한 리뷰를 숨김 처리합니다." />
    <PagePanel title="리뷰 목록" endpoint="GET /admin/reviews, PATCH /admin/reviews/{reviewId}/hide">
      <p v-if="feedbackMessage" class="info-banner">{{ feedbackMessage }}</p>
      <p v-if="errorMessage" class="error-banner">{{ errorMessage }}</p>
      <p v-if="loading" class="state-copy">리뷰 목록을 불러오는 중입니다.</p>
      <div v-else class="table-wrap">
        <table class="admin-table">
          <thead>
            <tr>
              <th>리뷰 ID</th>
              <th>메뉴 ID</th>
              <th>별점</th>
              <th>상태</th>
              <th>AI 생성</th>
              <th>내용</th>
              <th>작성일</th>
              <th>액션</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="review in reviews" :key="review.reviewId">
              <td>{{ review.reviewId }}</td>
              <td>{{ review.menuId }}</td>
              <td>{{ review.rating }}</td>
              <td>{{ review.status }}</td>
              <td>{{ review.aiGenerated ? "Y" : "N" }}</td>
              <td>{{ review.content }}</td>
              <td>{{ formatDateTime(review.createdAt) }}</td>
              <td>
                <button
                  type="button"
                  class="ghost-button"
                  :disabled="review.status !== 'ACTIVE'"
                  @click="hideReview(review.reviewId)"
                >
                  숨김 처리
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </PagePanel>
  </div>
</template>
