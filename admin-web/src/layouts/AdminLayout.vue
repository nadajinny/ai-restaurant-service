<script setup>
import AppShell from "@/components/AppShell.vue";
import { adminNavigation } from "@/navigation/adminNavigation";
import { useRoute } from "vue-router";

const route = useRoute();
</script>

<template>
  <AppShell
    brand="Admin"
    title="Restaurant Ops"
    subtitle="운영 지표, 재고, 주문, 리뷰, 쿠폰을 관리하는 관리자용 웹"
    :navigation="adminNavigation"
    theme="admin"
  >
    <template #header>
      <div>
        <p class="layout-kicker">Operations Console</p>
        <h1 class="layout-title">{{ route.meta.title ?? "관리자 화면" }}</h1>
      </div>
    </template>
    <router-view />
  </AppShell>
</template>
