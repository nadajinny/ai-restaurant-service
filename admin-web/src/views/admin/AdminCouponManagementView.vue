<script setup>
import { adminCouponApi } from "@/api";
import PageHero from "@/components/PageHero.vue";
import PagePanel from "@/components/PagePanel.vue";
import { formatCurrency, formatDateTime } from "@/utils/format";
import { onMounted, reactive, ref } from "vue";

const coupons = ref([]);
const loading = ref(false);
const submitting = ref(false);
const errorMessage = ref("");
const feedbackMessage = ref("");
const editingCouponId = ref(null);

const form = reactive({
  code: "",
  name: "",
  discountAmount: 1000,
  discountRate: null,
  maxDiscountAmount: null,
  minOrderAmount: 0,
  availableFrom: "",
  availableTo: "",
  availableCount: 100,
  active: true,
});

function resetForm() {
  editingCouponId.value = null;
  form.code = "";
  form.name = "";
  form.discountAmount = 1000;
  form.discountRate = null;
  form.maxDiscountAmount = null;
  form.minOrderAmount = 0;
  form.availableFrom = "";
  form.availableTo = "";
  form.availableCount = 100;
  form.active = true;
}

function fillForm(coupon) {
  editingCouponId.value = coupon.couponId;
  form.code = coupon.code;
  form.name = coupon.name;
  form.discountAmount = coupon.discountAmount;
  form.discountRate = coupon.discountRate;
  form.maxDiscountAmount = coupon.maxDiscountAmount;
  form.minOrderAmount = coupon.minOrderAmount;
  form.availableFrom = coupon.availableFrom?.slice(0, 16) ?? "";
  form.availableTo = coupon.availableTo?.slice(0, 16) ?? "";
  form.availableCount = coupon.availableCount;
  form.active = coupon.active;
}

function toPayload() {
  return {
    ...form,
    discountAmount: form.discountAmount || null,
    discountRate: form.discountRate || null,
    maxDiscountAmount: form.maxDiscountAmount || null,
    availableFrom: form.availableFrom,
    availableTo: form.availableTo,
  };
}

async function fetchCoupons() {
  loading.value = true;
  errorMessage.value = "";

  try {
    coupons.value = await adminCouponApi.getCoupons();
  } catch (error) {
    errorMessage.value = error.message ?? "쿠폰 목록을 불러오지 못했습니다.";
  } finally {
    loading.value = false;
  }
}

async function submitCoupon() {
  submitting.value = true;
  errorMessage.value = "";

  try {
    if (editingCouponId.value) {
      await adminCouponApi.updateCoupon(editingCouponId.value, toPayload());
      feedbackMessage.value = "쿠폰을 수정했습니다.";
    } else {
      await adminCouponApi.createCoupon(toPayload());
      feedbackMessage.value = "쿠폰을 생성했습니다.";
    }

    resetForm();
    await fetchCoupons();
  } catch (error) {
    errorMessage.value = error.message ?? "쿠폰 저장에 실패했습니다.";
  } finally {
    submitting.value = false;
  }
}

async function disableCoupon(couponId) {
  try {
    await adminCouponApi.disableCoupon(couponId);
    feedbackMessage.value = "쿠폰을 비활성화했습니다.";
    await fetchCoupons();
  } catch (error) {
    errorMessage.value = error.message ?? "쿠폰 비활성화에 실패했습니다.";
  }
}

onMounted(fetchCoupons);
</script>

<template>
  <div class="page-stack">
    <PageHero badge="Admin Coupons" title="쿠폰 관리" description="쿠폰 생성, 수정, 비활성화를 처리합니다." />

    <PagePanel title="쿠폰 등록 / 수정" endpoint="GET, POST, PUT, PATCH /admin/coupons">
      <p v-if="feedbackMessage" class="info-banner">{{ feedbackMessage }}</p>
      <p v-if="errorMessage" class="error-banner">{{ errorMessage }}</p>

      <div class="admin-form-grid">
        <label class="field-stack">
          <span>코드</span>
          <input v-model="form.code" class="app-field" type="text" />
        </label>
        <label class="field-stack">
          <span>이름</span>
          <input v-model="form.name" class="app-field" type="text" />
        </label>
        <label class="field-stack">
          <span>할인 금액</span>
          <input v-model.number="form.discountAmount" class="app-field" type="number" min="0" />
        </label>
        <label class="field-stack">
          <span>할인율</span>
          <input v-model.number="form.discountRate" class="app-field" type="number" min="0" max="100" />
        </label>
        <label class="field-stack">
          <span>최대 할인 금액</span>
          <input v-model.number="form.maxDiscountAmount" class="app-field" type="number" min="0" />
        </label>
        <label class="field-stack">
          <span>최소 주문 금액</span>
          <input v-model.number="form.minOrderAmount" class="app-field" type="number" min="0" />
        </label>
        <label class="field-stack">
          <span>사용 시작</span>
          <input v-model="form.availableFrom" class="app-field" type="datetime-local" />
        </label>
        <label class="field-stack">
          <span>사용 종료</span>
          <input v-model="form.availableTo" class="app-field" type="datetime-local" />
        </label>
        <label class="field-stack">
          <span>사용 가능 횟수</span>
          <input v-model.number="form.availableCount" class="app-field" type="number" min="1" />
        </label>
        <label class="field-stack">
          <span>활성화</span>
          <select v-model="form.active" class="app-field">
            <option :value="true">true</option>
            <option :value="false">false</option>
          </select>
        </label>
      </div>

      <div class="admin-actions">
        <button type="button" class="secondary-button" @click="resetForm">초기화</button>
        <button type="button" class="primary-button" :disabled="submitting" @click="submitCoupon">
          {{ submitting ? "저장 중" : editingCouponId ? "쿠폰 수정" : "쿠폰 생성" }}
        </button>
      </div>
    </PagePanel>

    <PagePanel title="쿠폰 목록">
      <p v-if="loading" class="state-copy">쿠폰 목록을 불러오는 중입니다.</p>
      <div v-else class="table-wrap">
        <table class="admin-table">
          <thead>
            <tr>
              <th>코드</th>
              <th>이름</th>
              <th>할인</th>
              <th>최소 주문 금액</th>
              <th>기간</th>
              <th>활성화</th>
              <th>액션</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="coupon in coupons" :key="coupon.couponId">
              <td>{{ coupon.code }}</td>
              <td>{{ coupon.name }}</td>
              <td>
                {{ coupon.discountAmount ? `${formatCurrency(coupon.discountAmount)}원` : `${coupon.discountRate}%` }}
              </td>
              <td>{{ formatCurrency(coupon.minOrderAmount) }}원</td>
              <td>{{ formatDateTime(coupon.availableFrom) }} ~ {{ formatDateTime(coupon.availableTo) }}</td>
              <td>{{ coupon.active ? "활성" : "비활성" }}</td>
              <td>
                <div class="table-actions">
                  <button type="button" class="secondary-button" @click="fillForm(coupon)">수정</button>
                  <button type="button" class="ghost-button" :disabled="!coupon.active" @click="disableCoupon(coupon.couponId)">
                    비활성화
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
