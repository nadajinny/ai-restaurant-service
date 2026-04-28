from fastapi import HTTPException, status

from app.schemas.common import MenuRecommendation


class QualityValidationService:
    def validate_recommendations(
        self,
        recommendations: list[MenuRecommendation],
        valid_menu_ids: set[int],
    ) -> list[MenuRecommendation]:
        filtered = [
            recommendation
            for recommendation in recommendations
            if recommendation.menuId in valid_menu_ids
            and recommendation.reason.strip()
            and recommendation.name.strip()
        ]

        if not filtered:
            raise HTTPException(
                status_code=status.HTTP_502_BAD_GATEWAY,
                detail="품질 검증에 실패한 추천 결과입니다.",
            )

        return filtered

    def validate_text_response(self, text: str) -> str:
        if len(text.strip()) < 5:
            raise HTTPException(
                status_code=status.HTTP_502_BAD_GATEWAY,
                detail="품질 검증에 실패한 텍스트 응답입니다.",
            )

        return text.strip()
