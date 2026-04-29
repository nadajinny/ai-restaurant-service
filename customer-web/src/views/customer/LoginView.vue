<script setup>
import { authApi } from "@/api";
import { setAuthSession } from "@/auth/authSession";
import { reactive, ref } from "vue";
import { useRoute, useRouter } from "vue-router";

const route = useRoute();
const router = useRouter();

const form = reactive({
  loginId: "user01",
  password: "password",
});
const loading = ref(false);
const errorMessage = ref("");

async function submitLogin() {
  loading.value = true;
  errorMessage.value = "";

  try {
    const response = await authApi.login({ ...form });
    setAuthSession(response);

    const redirectTarget =
      typeof route.query.redirect === "string" && route.query.redirect
        ? route.query.redirect
        : "/";

    await router.replace(redirectTarget);
  } catch (error) {
    errorMessage.value = error.message ?? "로그인에 실패했습니다.";
  } finally {
    loading.value = false;
  }
}
</script>

<template>
  <div class="auth-shell">
    <section class="auth-card">
      <p class="page-hero__badge">Customer Login</p>
      <h1 class="auth-title">고객 로그인</h1>
      <p class="auth-description">
        주문, 리뷰, 즐겨찾기, 알림, 개인화 추천은 로그인 후 사용할 수 있습니다.
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

      <div class="auth-help">
        <strong>기본 계정</strong>
        <span>user01 / password</span>
      </div>
    </section>
  </div>
</template>
