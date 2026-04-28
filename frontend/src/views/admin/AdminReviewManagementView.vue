<script setup>
import PageHero from "@/components/PageHero.vue";
import PagePanel from "@/components/PagePanel.vue";
</script>

<template>
  <div class="page-stack">
    <PageHero
      badge="Admin Reviews"
      title="리뷰 관리 화면"
      description="메뉴별, 별점별, AI 생성 여부 기준으로 리뷰를 관리하는 화면입니다."
    />
    <PagePanel title="연결 API" endpoint="GET /admin/reviews, PATCH /admin/reviews/{reviewId}/hide" />
  </div>
</template>
