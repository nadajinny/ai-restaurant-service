<script setup>
import PageHero from "@/components/PageHero.vue";
import PagePanel from "@/components/PagePanel.vue";
</script>

<template>
  <div class="page-stack">
    <PageHero
      badge="Admin Inventory"
      title="재고 관리 화면"
      description="메뉴별 재고 수량과 품절 상태를 관리하는 운영 화면입니다."
    />
    <PagePanel title="연결 API" endpoint="GET /admin/inventories, PUT /admin/inventories/{menuId}, PATCH /admin/inventories/{menuId}/sold-out, PATCH /admin/inventories/{menuId}/available" />
  </div>
</template>
