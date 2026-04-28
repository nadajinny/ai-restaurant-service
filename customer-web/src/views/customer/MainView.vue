<script setup>
import PageHero from "@/components/PageHero.vue";
import PagePanel from "@/components/PagePanel.vue";
import ScreenBlueprint from "@/components/ScreenBlueprint.vue";

const sections = [
  {
    title: "메인 진입 카드",
    description: "카테고리, 인기 메뉴, 개인화 추천, AI 추천 진입 버튼을 배치합니다.",
    items: ["카테고리 목록", "인기 메뉴", "개인화 추천", "장바구니 바로가기"],
  },
  {
    title: "고객 활동 허브",
    description: "주문 상태와 알림, 최근 재주문 흐름을 메인에서 바로 이어지게 만듭니다.",
    items: ["주문 상태 버튼", "최근 주문", "알림 요약"],
  },
];
</script>

<template>
  <div class="page-stack">
    <PageHero
      badge="Customer Home"
      title="메인 화면"
      description="고객이 메뉴 탐색과 추천, 주문 흐름을 바로 시작할 수 있는 홈 화면 구조입니다."
    />
    <PagePanel
      title="구성 목표"
      description="메인 화면은 추천과 재주문, 실시간 주문 확인의 허브로 사용합니다."
    >
      <ScreenBlueprint :sections="sections" />
    </PagePanel>
  </div>
</template>
