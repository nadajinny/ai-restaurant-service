<script setup>
import PageHero from "@/components/PageHero.vue";
import PagePanel from "@/components/PagePanel.vue";
</script>

<template>
  <div class="page-stack">
    <PageHero
      badge="Favorites"
      title="즐겨찾기 화면"
      description="저장한 메뉴를 빠르게 재탐색하고 장바구니로 보내는 고객용 목록 화면입니다."
    />
    <PagePanel title="연결 API" endpoint="GET /favorites, POST /favorites, DELETE /favorites/{menuId}" />
  </div>
</template>
