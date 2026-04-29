<script setup>
import { notificationApi } from "@/api";
import PageHero from "@/components/PageHero.vue";
import PagePanel from "@/components/PagePanel.vue";
import { useCurrentUser } from "@/composables/useCurrentUser";
import { onMounted, ref } from "vue";

const { ensureCurrentUser } = useCurrentUser();

const notifications = ref([]);
const loading = ref(false);
const errorMessage = ref("");
const currentUserId = ref(null);

function resolveNotificationLabel(type) {
  const labels = {
    ORDER_RECEIVED: "주문 접수",
    ORDER_COOKING: "조리 시작",
    ORDER_READY: "준비 완료",
    ORDER_COMPLETED: "주문 완료",
    ORDER_CANCELED: "주문 취소",
    PAYMENT_FAILED: "결제 실패",
  };

  return labels[type] ?? type;
}

function formatDateTime(value) {
  if (!value) {
    return "-";
  }

  return new Intl.DateTimeFormat("ko-KR", {
    dateStyle: "medium",
    timeStyle: "short",
  }).format(new Date(value));
}

async function fetchNotifications() {
  loading.value = true;
  errorMessage.value = "";

  try {
    const user = await ensureCurrentUser();
    currentUserId.value = user.id;
    notifications.value = await notificationApi.getNotifications();
  } catch (error) {
    errorMessage.value = error.message ?? "알림 목록을 불러오지 못했습니다.";
  } finally {
    loading.value = false;
  }
}

async function markAsRead(notificationId) {
  if (!currentUserId.value) {
    return;
  }

  try {
    await notificationApi.readNotification(notificationId);
    notifications.value = notifications.value.map((item) =>
      item.notificationId === notificationId ? { ...item, read: true } : item,
    );
  } catch (error) {
    errorMessage.value = error.message ?? "알림 읽음 처리에 실패했습니다.";
  }
}

onMounted(fetchNotifications);
</script>

<template>
  <div class="page-stack">
    <PageHero
      badge="Notifications"
      title="알림 화면"
      description="주문 상태 변경, 준비 완료, 결제 실패 알림을 확인하고 읽음 처리할 수 있습니다."
    />
    <PagePanel
      title="알림 목록"
      endpoint="GET /notifications, PATCH /notifications/{notificationId}/read"
      description="읽지 않은 알림을 우선 확인하고 개별 읽음 처리를 지원합니다."
    >
      <p v-if="errorMessage" class="error-banner">{{ errorMessage }}</p>
      <p v-if="loading" class="state-copy">알림을 불러오는 중입니다.</p>
      <p v-else-if="notifications.length === 0" class="state-copy">도착한 알림이 없습니다.</p>

      <div v-else class="notification-list">
        <article
          v-for="notification in notifications"
          :key="notification.notificationId"
          class="notification-card"
          :data-read="notification.read"
        >
          <div class="notification-card__top">
            <strong>{{ resolveNotificationLabel(notification.type) }}</strong>
            <span>{{ formatDateTime(notification.createdAt) }}</span>
          </div>
          <p class="notification-card__content">{{ notification.content }}</p>
          <button
            v-if="!notification.read"
            type="button"
            class="secondary-button"
            @click="markAsRead(notification.notificationId)"
          >
            읽음 처리
          </button>
          <span v-else class="review-badge">읽음 완료</span>
        </article>
      </div>
    </PagePanel>
  </div>
</template>
