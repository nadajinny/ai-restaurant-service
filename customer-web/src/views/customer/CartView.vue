<script setup>
import { menuApi } from "@/api";
import PageHero from "@/components/PageHero.vue";
import PagePanel from "@/components/PagePanel.vue";
import { useCart } from "@/composables/useCart";
import { formatCurrency } from "@/utils/format";
import { computed, onMounted, ref } from "vue";

const { cartItems, totalPrice, updateQuantity, removeItem, syncMenuStatuses } = useCart();
const loading = ref(false);
const errorMessage = ref("");

const unavailableCount = computed(
  () => cartItems.value.filter((item) => !item.orderable).length,
);

async function refreshCartMenus() {
  loading.value = true;
  errorMessage.value = "";

  try {
    const menus = await menuApi.getMenus();
    syncMenuStatuses(menus ?? []);
  } catch (error) {
    errorMessage.value = error.message ?? "최신 메뉴 상태를 확인하지 못했습니다.";
  } finally {
    loading.value = false;
  }
}

function increaseQuantity(item) {
  updateQuantity(item.menuId, item.quantity + 1);
}

function decreaseQuantity(item) {
  updateQuantity(item.menuId, item.quantity - 1);
}

onMounted(refreshCartMenus);
</script>

<template>
  <div class="page-stack">
    <PageHero
      badge="Cart"
      title="장바구니 화면"
      description="localStorage 기반 장바구니를 유지하고 수량, 삭제, 총액을 바로 확인할 수 있습니다."
    />
    <PagePanel
      title="장바구니 목록"
      endpoint="localStorage + GET /menus"
      description="메뉴 최신 상태를 조회해 품절 또는 판매 중지 메뉴는 주문 불가로 표시합니다."
    >
      <p v-if="errorMessage" class="error-banner">{{ errorMessage }}</p>
      <p v-if="loading" class="state-copy">최신 메뉴 상태를 확인하는 중입니다.</p>
      <p v-if="cartItems.length === 0" class="state-copy">장바구니가 비어 있습니다.</p>

      <template v-else>
        <p v-if="unavailableCount > 0" class="warning-banner">
          주문 불가 메뉴 {{ unavailableCount }}개가 포함되어 있습니다. 품절 또는 판매 중지 메뉴는 주문 전에 제거해야 합니다.
        </p>

        <div class="cart-grid">
          <article v-for="item in cartItems" :key="item.menuId" class="cart-item">
            <img :src="item.imageUrl" :alt="item.name" class="cart-item__image" />
            <div class="cart-item__content">
              <div class="cart-item__header">
                <div>
                  <p class="menu-card__category">{{ item.category }}</p>
                  <h3>{{ item.name }}</h3>
                </div>
                <span class="menu-status" :data-status="item.status">
                  {{ item.orderable ? "주문 가능" : item.status === "SOLD_OUT" ? "품절" : "판매 중지" }}
                </span>
              </div>

              <p class="menu-card__meta">
                단가 {{ formatCurrency(item.price) }}원 · 조리 {{ item.cookingTime }}분
              </p>

              <div class="cart-item__footer">
                <div class="quantity-stepper">
                  <button type="button" class="step-button" @click="decreaseQuantity(item)">-</button>
                  <span class="quantity-stepper__value">{{ item.quantity }}</span>
                  <button type="button" class="step-button" @click="increaseQuantity(item)">+</button>
                </div>

                <strong class="cart-item__total">
                  {{ formatCurrency(item.price * item.quantity) }}원
                </strong>

                <button type="button" class="ghost-button" @click="removeItem(item.menuId)">
                  삭제
                </button>
              </div>
            </div>
          </article>
        </div>

        <div class="cart-summary">
          <div>
            <span>총 금액</span>
            <strong>{{ formatCurrency(totalPrice) }}원</strong>
          </div>
          <router-link to="/menus" class="secondary-button">메뉴 더 보기</router-link>
        </div>
      </template>
    </PagePanel>
  </div>
</template>
