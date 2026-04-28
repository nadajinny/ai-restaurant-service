from collections.abc import Sequence

from app.clients.backend_menu_client import MenuCatalogItem
from app.schemas.admin_ai import NewMenuRecommendation
from app.schemas.common import MenuRecommendation


class MockAiService:
    def recommend_by_message(
        self,
        message: str,
        menus: Sequence[MenuCatalogItem],
    ) -> list[MenuRecommendation]:
        selected = list(menus[:3])
        return [
            MenuRecommendation(
                menuId=menu.menu_id,
                name=menu.name,
                reason=f"'{message}' 요청과 잘 맞는 {menu.category} 카테고리 메뉴입니다.",
            )
            for menu in selected
        ]

    def recommend_by_emotion(
        self,
        emotion: str,
        context: str | None,
        menus: Sequence[MenuCatalogItem],
    ) -> list[MenuRecommendation]:
        reason_suffix = f" 현재 감정이 '{emotion}'이고 맥락은 '{context}'로 해석되었습니다." if context else f" 현재 감정이 '{emotion}'로 해석되었습니다."
        selected = list(menus[:3])
        return [
            MenuRecommendation(
                menuId=menu.menu_id,
                name=menu.name,
                reason=f"감정 기반 추천 메뉴입니다.{reason_suffix}",
            )
            for menu in selected
        ]

    def personalized_recommendations(
        self,
        user_id: int | None,
        menus: Sequence[MenuCatalogItem],
    ) -> list[MenuRecommendation]:
        selected = list(menus[:3])
        reason = "주문 이력과 선호 카테고리를 반영한 Mock 개인화 추천입니다."
        return [
            MenuRecommendation(menuId=menu.menu_id, name=menu.name, reason=reason)
            for menu in selected
        ]

    def generate_review_draft(self, menu_name: str, keywords: list[str]) -> str:
        joined_keywords = ", ".join(keywords)
        return f"{menu_name}은(는) {joined_keywords} 느낌이 잘 살아 있어서 만족스러웠습니다."

    def summarize_reviews(self, menu_name: str) -> str:
        return f"{menu_name} 리뷰 요약 Mock 결과입니다. 맛과 양, 가성비에 대한 긍정 의견이 많았습니다."

    def new_menu_recommendations(self) -> list[NewMenuRecommendation]:
        return [
            NewMenuRecommendation(
                name="청양 제육 덮밥",
                category="KOREAN",
                reason="매운맛 선호와 덮밥류 수요가 꾸준하다는 가정의 Mock 신메뉴 제안입니다.",
            ),
            NewMenuRecommendation(
                name="트러플 크림 돈까스",
                category="JAPANESE",
                reason="기존 돈까스 계열 확장 메뉴로 객단가 상승을 기대하는 Mock 제안입니다.",
            ),
        ]
