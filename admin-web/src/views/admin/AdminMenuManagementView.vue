<script setup>
import PageHero from "@/components/PageHero.vue";
import PagePanel from "@/components/PagePanel.vue";
</script>

<template>
  <div class="page-stack">
    <PageHero
      badge="Admin Menus"
      title="메뉴 관리 화면"
      description="등록, 수정, 삭제, 판매 상태 변경을 처리하는 관리자용 메뉴 테이블 구조입니다."
    />
    <PagePanel title="연결 API" endpoint="POST /admin/menus, PUT /admin/menus/{menuId}, DELETE /admin/menus/{menuId}, PATCH /admin/menus/{menuId}/status" />
  </div>
</template>
