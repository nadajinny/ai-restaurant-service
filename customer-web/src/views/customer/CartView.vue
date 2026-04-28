<script setup>
import PageHero from "@/components/PageHero.vue";
import PagePanel from "@/components/PagePanel.vue";
</script>

<template>
  <div class="page-stack">
    <PageHero
      badge="Cart"
      title="장바구니 화면"
      description="localStorage 장바구니와 서버 주문 생성 API를 연결할 기본 화면입니다."
    />
    <PagePanel
      title="연결 API"
      endpoint="POST /orders"
      description="선택 메뉴, 수량, 쿠폰 입력, 최종 주문 금액 확인 UI를 배치합니다."
    >
      <ul class="bullet-list">
        <li>장바구니 항목 목록과 수량 변경</li>
        <li>총액, 할인, 최종 결제 금액 요약</li>
        <li>품절 및 가격 변경 안내 영역</li>
      </ul>
    </PagePanel>
  </div>
</template>
