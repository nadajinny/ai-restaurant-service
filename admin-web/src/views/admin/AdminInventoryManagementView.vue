<script setup>
import { adminInventoryApi } from "@/api";
import PageHero from "@/components/PageHero.vue";
import PagePanel from "@/components/PagePanel.vue";
import { onMounted, ref } from "vue";

const inventories = ref([]);
const loading = ref(false);
const errorMessage = ref("");
const feedbackMessage = ref("");

async function fetchInventories() {
  loading.value = true;
  errorMessage.value = "";

  try {
    inventories.value = await adminInventoryApi.getInventories();
  } catch (error) {
    errorMessage.value = error.message ?? "재고 목록을 불러오지 못했습니다.";
  } finally {
    loading.value = false;
  }
}

async function updateQuantity(item) {
  try {
    await adminInventoryApi.updateInventory(item.menuId, item.quantity);
    feedbackMessage.value = "재고 수량을 수정했습니다.";
    await fetchInventories();
  } catch (error) {
    errorMessage.value = error.message ?? "재고 수량 수정에 실패했습니다.";
  }
}

async function markSoldOut(menuId) {
  try {
    await adminInventoryApi.markSoldOut(menuId);
    feedbackMessage.value = "품절 처리했습니다.";
    await fetchInventories();
  } catch (error) {
    errorMessage.value = error.message ?? "품절 처리에 실패했습니다.";
  }
}

async function markAvailable(menuId) {
  try {
    await adminInventoryApi.markAvailable(menuId);
    feedbackMessage.value = "판매 가능 상태로 변경했습니다.";
    await fetchInventories();
  } catch (error) {
    errorMessage.value = error.message ?? "판매 가능 상태 변경에 실패했습니다.";
  }
}

onMounted(fetchInventories);
</script>

<template>
  <div class="page-stack">
    <PageHero badge="Admin Inventory" title="재고 관리" description="재고 수량 수정과 품절/판매 가능 전환을 처리합니다." />
    <PagePanel title="재고 목록">
      <p v-if="feedbackMessage" class="info-banner">{{ feedbackMessage }}</p>
      <p v-if="errorMessage" class="error-banner">{{ errorMessage }}</p>
      <p v-if="loading" class="state-copy">재고 목록을 불러오는 중입니다.</p>

      <div v-else class="table-wrap">
        <table class="admin-table">
          <thead>
            <tr>
              <th>메뉴명</th>
              <th>카테고리</th>
              <th>재고</th>
              <th>상태</th>
              <th>액션</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in inventories" :key="item.menuId">
              <td>{{ item.menuName }}</td>
              <td>{{ item.category }}</td>
              <td>
                <input v-model.number="item.quantity" class="app-field app-field--small" type="number" min="0" />
              </td>
              <td>{{ item.status }}</td>
              <td>
                <div class="table-actions">
                  <button type="button" class="secondary-button" @click="updateQuantity(item)">수량 저장</button>
                  <button type="button" class="ghost-button" @click="markSoldOut(item.menuId)">품절</button>
                  <button type="button" class="tiny-button" @click="markAvailable(item.menuId)">판매 가능</button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </PagePanel>
  </div>
</template>
