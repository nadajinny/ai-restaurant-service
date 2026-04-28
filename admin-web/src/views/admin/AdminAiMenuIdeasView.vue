<script setup>
import PageHero from "@/components/PageHero.vue";
import PagePanel from "@/components/PagePanel.vue";
</script>

<template>
  <div class="page-stack">
    <PageHero
      badge="Admin AI"
      title="AI 신메뉴 추천 화면"
      description="AI가 제안한 신메뉴 후보와 추천 이유를 검토하는 관리자용 화면입니다."
    />
    <PagePanel title="연결 API" endpoint="GET /admin/ai/new-menu-recommendations" />
  </div>
</template>
