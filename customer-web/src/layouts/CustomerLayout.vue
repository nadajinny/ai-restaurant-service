<script setup>
import { authState, clearAuthSession } from "@/auth/authSession";
import AppShell from "@/components/AppShell.vue";
import { customerNavigation } from "@/navigation/customerNavigation";
import { computed } from "vue";
import { useRoute } from "vue-router";

const route = useRoute();
const currentUser = computed(() => authState.value.user);

function logout() {
  clearAuthSession();
  window.location.href = "/login";
}
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
      <div class="layout-header-row">
        <div>
          <p class="layout-kicker">Customer Experience</p>
          <h1 class="layout-title">{{ route.meta.title ?? "고객 화면" }}</h1>
        </div>
        <div class="layout-session">
          <template v-if="currentUser">
            <span>{{ currentUser.name }} · {{ currentUser.role }}</span>
            <button type="button" class="secondary-button" @click="logout">로그아웃</button>
          </template>
          <router-link v-else to="/login" class="secondary-button">로그인</router-link>
        </div>
      </div>
    </template>
    <router-view />
  </AppShell>
</template>
