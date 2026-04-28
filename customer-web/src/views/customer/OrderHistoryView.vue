<script setup>
import PageHero from "@/components/PageHero.vue";
import PagePanel from "@/components/PagePanel.vue";
</script>

<template>
  <div class="page-stack">
    <PageHero
      badge="Order History"
      title="주문 이력 화면"
      description="과거 주문 목록, 재주문, 리뷰 작성으로 이어지는 고객 이력 화면입니다."
    />
    <PagePanel title="연결 API" endpoint="GET /orders?userId={userId}" description="최신순 주문 목록, 재주문 버튼, 리뷰 작성 진입을 연결합니다." />
  </div>
</template>
