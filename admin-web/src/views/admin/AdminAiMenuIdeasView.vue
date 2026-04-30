<script setup>
import { aiApi } from "@/api";
import PageHero from "@/components/PageHero.vue";
import PagePanel from "@/components/PagePanel.vue";
import { onMounted, ref } from "vue";

const loading = ref(false);
const errorMessage = ref("");
const recommendations = ref([]);

async function fetchRecommendations() {
  loading.value = true;
  errorMessage.value = "";

  try {
    const response = await aiApi.getNewMenuRecommendations();
    recommendations.value = response.recommendations ?? [];
  } catch (error) {
    errorMessage.value = error.message ?? "AI 신메뉴 추천을 불러오지 못했습니다.";
  } finally {
    loading.value = false;
  }
}

onMounted(fetchRecommendations);
</script>

<template>
  <div class="page-stack">
    <PageHero badge="Admin AI" title="AI 신메뉴 추천" description="AI가 제안한 신메뉴 후보와 추천 이유를 검토합니다." />
    <PagePanel title="신메뉴 추천 목록">
      <p v-if="errorMessage" class="error-banner">{{ errorMessage }}</p>
      <p v-if="loading" class="state-copy">AI 추천 결과를 불러오는 중입니다.</p>
      <p v-else-if="recommendations.length === 0" class="state-copy">추천 결과가 없습니다.</p>

      <div v-else class="admin-grid admin-grid--three">
        <article v-for="item in recommendations" :key="item.name" class="data-card">
          <h3>{{ item.name }}</h3>
          <p class="muted-copy">{{ item.category }}</p>
          <p>{{ item.reason }}</p>
        </article>
      </div>
    </PagePanel>
  </div>
</template>
