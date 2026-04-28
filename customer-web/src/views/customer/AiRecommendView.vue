<script setup>
import PageHero from "@/components/PageHero.vue";
import PagePanel from "@/components/PagePanel.vue";
import ScreenBlueprint from "@/components/ScreenBlueprint.vue";

const sections = [
  {
    title: "AI 메뉴 추천",
    description: "자연어와 감정 기반 추천을 같은 화면에서 다룰 수 있도록 설계합니다.",
    items: ["자연어 입력", "감정 입력", "추천 결과 카드", "추천 이유"],
  },
  {
    title: "개인화 추천",
    description: "개인화 추천 결과를 별도 섹션으로 제공해 메인 추천과 구분합니다.",
    items: ["사용자별 추천", "즐겨찾기 기반 추천", "장바구니 추가 버튼"],
  },
];
</script>

<template>
  <div class="page-stack">
    <PageHero
      badge="AI"
      title="AI 추천 화면"
      description="추천, 감정 기반 추천, 개인화 추천, AI 리뷰 생성 진입을 묶는 화면 구조입니다."
    />
    <PagePanel title="연결 API" endpoint="POST /ai/recommend, GET /ai/personalized-recommendations, POST /ai/emotion-recommend">
      <ScreenBlueprint :sections="sections" />
    </PagePanel>
  </div>
</template>
