<script setup>
import PageHero from "@/components/PageHero.vue";
import PagePanel from "@/components/PagePanel.vue";
</script>

<template>
  <div class="page-stack">
    <PageHero
      badge="Order Tracking"
      title="주문 상태 화면"
      description="접수부터 완료까지 주문 상태를 단계적으로 보여주는 고객용 화면입니다."
    />
    <PagePanel title="연결 API" endpoint="GET /orders/{orderId}?userId={userId}" description="상태, 주문 항목, 예상 완료 시간, 알림 연계를 표시합니다." />
  </div>
</template>
