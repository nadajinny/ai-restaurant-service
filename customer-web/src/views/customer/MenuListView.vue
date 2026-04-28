<script setup>
import PageHero from "@/components/PageHero.vue";
import PagePanel from "@/components/PagePanel.vue";
import ScreenBlueprint from "@/components/ScreenBlueprint.vue";

const sections = [
  {
    title: "필터 바",
    description: "카테고리, 가격 범위, 상태, 정렬을 한 영역에 모읍니다.",
    items: ["카테고리 선택", "가격 범위", "정렬 옵션", "품절 제외 토글"],
  },
  {
    title: "메뉴 카드 그리드",
    description: "판매 상태와 가격, 조리 시간, 즐겨찾기 액션을 카드에서 확인합니다.",
    items: ["메뉴 이미지", "메뉴명", "가격", "판매 상태", "상세 이동"],
  },
];
</script>

<template>
  <div class="page-stack">
    <PageHero
      badge="Menus"
      title="메뉴 목록 화면"
      description="검색, 필터, 정렬, 장바구니 진입이 한 화면 안에서 이어지는 메뉴 탐색 구조입니다."
    />
    <PagePanel title="연결 API" endpoint="GET /menus" description="고객용 메뉴 목록, 카테고리, 정렬, 가격 필터를 연결합니다.">
      <ScreenBlueprint :sections="sections" />
    </PagePanel>
  </div>
</template>
