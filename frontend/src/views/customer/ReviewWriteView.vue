<script setup>
import PageHero from "@/components/PageHero.vue";
import PagePanel from "@/components/PagePanel.vue";
</script>

<template>
  <div class="page-stack">
    <PageHero
      badge="Review"
      title="리뷰 작성 화면"
      description="수동 작성과 AI 후기 초안 생성을 함께 다루는 리뷰 입력 구조입니다."
    />
    <PagePanel
      title="연결 API"
      endpoint="POST /reviews, POST /ai/review-generate"
      description="별점, 본문, AI 생성 여부, 키워드 선택 UI를 배치합니다."
    />
  </div>
</template>
