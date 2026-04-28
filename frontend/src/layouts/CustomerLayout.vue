<script setup>
import AppShell from "@/components/AppShell.vue";
import { customerNavigation } from "@/navigation/customerNavigation";
import { useRoute } from "vue-router";

const route = useRoute();
</script>

<template>
  <AppShell
    brand="Customer"
    title="AI Restaurant"
    subtitle="메뉴 탐색, 주문, 후기, 추천 기능을 한 흐름으로 사용하는 고객용 웹"
    :navigation="customerNavigation"
    theme="customer"
  >
    <template #header>
      <div>
        <p class="layout-kicker">Customer Experience</p>
        <h1 class="layout-title">{{ route.meta.title ?? "고객 화면" }}</h1>
      </div>
    </template>
    <router-view />
  </AppShell>
</template>
