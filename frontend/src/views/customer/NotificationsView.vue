<script setup>
import PageHero from "@/components/PageHero.vue";
import PagePanel from "@/components/PagePanel.vue";
</script>

<template>
  <div class="page-stack">
    <PageHero
      badge="Notifications"
      title="알림 화면"
      description="주문 상태 변경, 결제 실패, 준비 완료 알림을 읽음 처리하는 화면입니다."
    />
    <PagePanel title="연결 API" endpoint="GET /notifications, PATCH /notifications/{notificationId}/read" />
  </div>
</template>
