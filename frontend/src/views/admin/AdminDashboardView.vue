<script setup>
import PageHero from "@/components/PageHero.vue";
import PagePanel from "@/components/PagePanel.vue";
import ScreenBlueprint from "@/components/ScreenBlueprint.vue";

const sections = [
  {
    title: "운영 KPI",
    description: "당일 주문 수, 당일 매출, 인기 메뉴, 시간대별 주문량을 첫 화면에서 확인합니다.",
    items: ["당일 주문 수", "당일 매출", "인기 메뉴", "시간대별 주문량"],
  },
  {
    title: "운영 리스크",
    description: "품절 메뉴와 최근 리뷰를 함께 노출해 운영자가 바로 대응할 수 있게 합니다.",
    items: ["품절 메뉴 목록", "최근 리뷰", "AI 신메뉴 제안"],
  },
];
</script>

<template>
  <div class="page-stack">
    <PageHero
      badge="Dashboard"
      title="관리자 대시보드"
      description="주요 운영 지표와 이슈를 한 화면에 모아 보여주는 관리자 홈입니다."
    />
    <PagePanel title="연결 API" endpoint="GET /admin/dashboard">
      <ScreenBlueprint :sections="sections" />
    </PagePanel>
  </div>
</template>
