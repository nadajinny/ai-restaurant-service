<script setup>
import { authApi } from "@/api";
import { clearAuthSession, setAuthSession } from "@/auth/authSession";
import { reactive, ref } from "vue";
import { useRoute, useRouter } from "vue-router";

const route = useRoute();
const router = useRouter();

const form = reactive({
  loginId: "",
  password: "",
});
const loading = ref(false);
const errorMessage = ref("");

async function submitLogin() {
  loading.value = true;
  errorMessage.value = "";

  try {
    const response = await authApi.login({ ...form });
    if (response.user.role !== "ADMIN") {
      clearAuthSession();
      errorMessage.value = "관리자 계정만 접근할 수 있습니다.";
      return;
    }

    setAuthSession(response);

    const redirectTarget =
      typeof route.query.redirect === "string" && route.query.redirect
        ? route.query.redirect
        : "/admin";

    await router.replace(redirectTarget);
  } catch (error) {
    errorMessage.value = error.message ?? "로그인에 실패했습니다.";
  } finally {
    loading.value = false;
  }
}
</script>

<template>
  <div class="auth-shell auth-shell--admin">
    <div class="auth-shell__grid">
      <aside class="auth-aside">
        <p class="page-hero__badge">Operations Console</p>
        <h1 class="auth-aside__title">Restaurant Ops</h1>
        <p class="auth-aside__description">
          운영 현황, 메뉴, 주문, 재고, 쿠폰, 리뷰를 한 화면 흐름으로 관리하는 관리자 전용 콘솔입니다.
        </p>
        <ul class="auth-aside__list">
          <li>실시간 운영 지표 확인</li>
          <li>메뉴 및 재고 상태 조정</li>
          <li>주문 상태와 고객 피드백 관리</li>
        </ul>
      </aside>

      <section class="auth-card auth-card--admin">
        <p class="page-hero__badge">Admin Login</p>
        <h2 class="auth-title">관리자 로그인</h2>
        <p class="auth-description">
          운영 지표, 메뉴, 주문, 리뷰, 재고, 쿠폰 관리 기능은 관리자만 접근할 수 있습니다.
        </p>

        <p v-if="errorMessage" class="error-banner">{{ errorMessage }}</p>

        <form class="auth-form" @submit.prevent="submitLogin">
          <label class="field-stack">
            <span>로그인 ID</span>
            <input v-model="form.loginId" class="app-field" type="text" autocomplete="username" />
          </label>
          <label class="field-stack">
            <span>비밀번호</span>
            <input v-model="form.password" class="app-field" type="password" autocomplete="current-password" />
          </label>

          <button type="submit" class="primary-button" :disabled="loading">
            {{ loading ? "로그인 중" : "로그인" }}
          </button>
        </form>
      </section>
    </div>
  </div>
</template>
