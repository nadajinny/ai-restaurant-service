<script setup>
import PageHero from "@/components/PageHero.vue";
import PagePanel from "@/components/PagePanel.vue";
</script>

<template>
  <div class="page-stack">
    <PageHero
      badge="Admin Sales"
      title="매출 관리 화면"
      description="완료 주문 기준 매출, 메뉴 성과, 인기 메뉴, 시간대별 주문량을 다루는 화면입니다."
    />
    <PagePanel title="연결 API" endpoint="GET /admin/analytics/sales, GET /admin/analytics/popular-menus, GET /admin/analytics/menu-performance, GET /admin/analytics/hourly-orders" />
  </div>
</template>
