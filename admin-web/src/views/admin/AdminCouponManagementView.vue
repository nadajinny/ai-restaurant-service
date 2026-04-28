<script setup>
import PageHero from "@/components/PageHero.vue";
import PagePanel from "@/components/PagePanel.vue";
</script>

<template>
  <div class="page-stack">
    <PageHero
      badge="Admin Coupons"
      title="쿠폰 관리 화면"
      description="쿠폰 생성, 수정, 비활성화, 적용 가능 조건 확인을 담당하는 화면입니다."
    />
    <PagePanel title="연결 API" endpoint="POST /admin/coupons, PUT /admin/coupons/{couponId}, PATCH /admin/coupons/{couponId}/disable, GET /coupons/available, POST /coupons/apply" />
  </div>
</template>
