<script setup>
import { adminMenuApi } from "@/api";
import PageHero from "@/components/PageHero.vue";
import PagePanel from "@/components/PagePanel.vue";
import { formatCurrency } from "@/utils/format";
import { computed, onMounted, reactive, ref } from "vue";

const menus = ref([]);
const loading = ref(false);
const submitting = ref(false);
const errorMessage = ref("");
const feedbackMessage = ref("");
const editingMenuId = ref(null);

const form = reactive({
  name: "",
  category: "KOREAN",
  price: 10000,
  description: "",
  imageUrl: "",
  cookingTime: 10,
  status: "AVAILABLE",
});

const statusOptions = ["AVAILABLE", "SOLD_OUT", "HIDDEN"];
const defaultCategoryOptions = ["KOREAN", "CHINESE", "JAPANESE", "WESTERN", "SNACK", "DESSERT"];
const categoryOptions = computed(() =>
  [
    ...new Set(
      [...defaultCategoryOptions, ...menus.value.map((menu) => menu.category), form.category].filter(Boolean),
    ),
  ],
);

function resetForm() {
  editingMenuId.value = null;
  form.name = "";
  form.category = "KOREAN";
  form.price = 10000;
  form.description = "";
  form.imageUrl = "";
  form.cookingTime = 10;
  form.status = "AVAILABLE";
}

function fillForm(menu) {
  editingMenuId.value = menu.menuId;
  form.name = menu.name;
  form.category = menu.category;
  form.price = menu.price;
  form.description = menu.description;
  form.imageUrl = menu.imageUrl;
  form.cookingTime = menu.cookingTime;
  form.status = menu.status;
}

async function fetchMenus() {
  loading.value = true;
  errorMessage.value = "";

  try {
    menus.value = await adminMenuApi.getMenus();
  } catch (error) {
    errorMessage.value = error.message ?? "메뉴 목록을 불러오지 못했습니다.";
  } finally {
    loading.value = false;
  }
}

async function submitMenu() {
  submitting.value = true;
  errorMessage.value = "";

  try {
    if (editingMenuId.value) {
      await adminMenuApi.updateMenu(editingMenuId.value, { ...form });
      feedbackMessage.value = "메뉴를 수정했습니다.";
    } else {
      await adminMenuApi.createMenu({ ...form });
      feedbackMessage.value = "메뉴를 등록했습니다.";
    }

    resetForm();
    await fetchMenus();
  } catch (error) {
    errorMessage.value = error.message ?? "메뉴 저장에 실패했습니다.";
  } finally {
    submitting.value = false;
  }
}

async function removeMenu(menuId) {
  try {
    await adminMenuApi.deleteMenu(menuId);
    feedbackMessage.value = "메뉴를 삭제하거나 비활성화했습니다.";
    await fetchMenus();
  } catch (error) {
    errorMessage.value = error.message ?? "메뉴 삭제에 실패했습니다.";
  }
}

async function changeStatus(menuId, status) {
  try {
    await adminMenuApi.updateMenuStatus(menuId, status);
    feedbackMessage.value = "메뉴 상태를 변경했습니다.";
    await fetchMenus();
  } catch (error) {
    errorMessage.value = error.message ?? "상태 변경에 실패했습니다.";
  }
}

onMounted(fetchMenus);
</script>

<template>
  <div class="page-stack">
    <PageHero badge="Admin Menus" title="메뉴 관리" description="메뉴 등록, 수정, 삭제, 상태 변경을 처리합니다." />

    <PagePanel title="메뉴 등록 / 수정">
      <p v-if="feedbackMessage" class="info-banner">{{ feedbackMessage }}</p>
      <p v-if="errorMessage" class="error-banner">{{ errorMessage }}</p>

      <div class="admin-form-grid">
        <label class="field-stack">
          <span>메뉴명</span>
          <input v-model="form.name" class="app-field" type="text" />
        </label>
        <label class="field-stack">
          <span>카테고리</span>
          <select v-model="form.category" class="app-field">
            <option v-for="category in categoryOptions" :key="category" :value="category">{{ category }}</option>
          </select>
        </label>
        <label class="field-stack">
          <span>가격</span>
          <input v-model.number="form.price" class="app-field" type="number" min="1" />
        </label>
        <label class="field-stack">
          <span>예상 조리 시간</span>
          <input v-model.number="form.cookingTime" class="app-field" type="number" min="1" />
        </label>
        <label class="field-stack admin-form-grid__wide">
          <span>이미지 URL</span>
          <input v-model="form.imageUrl" class="app-field" type="text" />
        </label>
        <label class="field-stack admin-form-grid__wide">
          <span>설명</span>
          <textarea v-model="form.description" class="app-field admin-textarea" rows="4" />
        </label>
        <label class="field-stack">
          <span>상태</span>
          <select v-model="form.status" class="app-field">
            <option v-for="status in statusOptions" :key="status" :value="status">{{ status }}</option>
          </select>
        </label>
      </div>

      <div class="admin-actions">
        <button type="button" class="secondary-button" @click="resetForm">초기화</button>
        <button type="button" class="primary-button" :disabled="submitting" @click="submitMenu">
          {{ submitting ? "저장 중" : editingMenuId ? "메뉴 수정" : "메뉴 등록" }}
        </button>
      </div>
    </PagePanel>

    <PagePanel title="메뉴 목록">
      <p v-if="loading" class="state-copy">메뉴 목록을 불러오는 중입니다.</p>
      <div v-else class="table-wrap">
        <table class="admin-table">
          <thead>
            <tr>
              <th>메뉴명</th>
              <th>카테고리</th>
              <th>가격</th>
              <th>상태</th>
              <th>액션</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="menu in menus" :key="menu.menuId">
              <td>{{ menu.name }}</td>
              <td>{{ menu.category }}</td>
              <td>{{ formatCurrency(menu.price) }}원</td>
              <td>{{ menu.status }}</td>
              <td>
                <div class="table-actions">
                  <button type="button" class="secondary-button" @click="fillForm(menu)">수정</button>
                  <button type="button" class="ghost-button" @click="removeMenu(menu.menuId)">삭제</button>
                  <button
                    v-for="status in statusOptions"
                    :key="`${menu.menuId}-${status}`"
                    type="button"
                    class="tiny-button"
                    @click="changeStatus(menu.menuId, status)"
                  >
                    {{ status }}
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </PagePanel>
  </div>
</template>
