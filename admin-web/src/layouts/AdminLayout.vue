<script setup>
import { authState, clearAuthSession } from "@/auth/authSession";
import AppShell from "@/components/AppShell.vue";
import { adminNavigation } from "@/navigation/adminNavigation";
import { computed } from "vue";
import { useRoute } from "vue-router";

const route = useRoute();
const currentUser = computed(() => authState.value.user);

function logout() {
  clearAuthSession();
  window.location.href = "/admin/login";
}
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
      <div class="layout-header-row">
        <div>
          <p class="layout-kicker">Operations Console</p>
          <h1 class="layout-title">{{ route.meta.title ?? "관리자 화면" }}</h1>
        </div>
        <div class="layout-session">
          <template v-if="currentUser">
            <div class="layout-session__card">
              <div class="layout-session__identity">
                <strong>{{ currentUser.name }}</strong>
                <span>{{ currentUser.role }} · secured session</span>
              </div>
              <div class="layout-session__actions">
                <span class="layout-pill">Live Ops</span>
                <button type="button" class="secondary-button" @click="logout">로그아웃</button>
              </div>
            </div>
          </template>
        </div>
      </div>
    </template>
    <router-view />
  </AppShell>
</template>
