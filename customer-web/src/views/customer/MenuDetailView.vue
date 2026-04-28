<script setup>
import PageHero from "@/components/PageHero.vue";
import PagePanel from "@/components/PagePanel.vue";

const details = [
  "메뉴 이미지, 이름, 가격, 설명, 예상 조리 시간",
  "판매 상태와 주문 가능 여부",
  "즐겨찾기 버튼과 장바구니 추가 버튼",
  "리뷰 목록과 AI 리뷰 요약 영역",
];
</script>

<template>
  <div class="page-stack">
    <PageHero
      badge="Menu Detail"
      title="메뉴 상세 화면"
      description="주문 여부를 결정하는 핵심 정보를 한 화면에 모으는 상세 구조입니다."
    />
    <PagePanel title="연결 API" endpoint="GET /menus/{menuId}" description="상세 정보와 리뷰, AI 리뷰 요약을 결합할 화면입니다.">
      <ul class="bullet-list">
        <li v-for="item in details" :key="item">{{ item }}</li>
      </ul>
    </PagePanel>
  </div>
</template>
