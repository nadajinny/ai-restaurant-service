<script setup>
import PageHero from "@/components/PageHero.vue";
import PagePanel from "@/components/PagePanel.vue";
</script>

<template>
  <div class="page-stack">
    <PageHero
      badge="Admin Orders"
      title="주문 관리 화면"
      description="주문 목록과 상세, 상태 변경, 취소 처리를 담당하는 관리자용 화면입니다."
    />
    <PagePanel title="연결 API" endpoint="GET /orders, GET /orders/{orderId}, PATCH /admin/orders/{orderId}/status" />
  </div>
</template>
