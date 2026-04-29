from fastapi import APIRouter, Depends

from app.dependencies import get_mock_ai_service, get_policy_service, get_quality_service
from app.schemas.admin_ai import NewMenuRecommendationsResponse
from app.services.ai_mock_service import MockAiService
from app.services.policy_service import PolicyValidationService
from app.services.quality_service import QualityValidationService

router = APIRouter(tags=["admin-ai"])


@router.get("/admin/ai/new-menu-recommendations", response_model=NewMenuRecommendationsResponse)
async def new_menu_recommendations(
    policy_service: PolicyValidationService = Depends(get_policy_service),
    quality_service: QualityValidationService = Depends(get_quality_service),
    mock_ai_service: MockAiService = Depends(get_mock_ai_service),
) -> NewMenuRecommendationsResponse:
    recommendations = mock_ai_service.new_menu_recommendations()
    for recommendation in recommendations:
        policy_service.validate_output_text(recommendation.reason)
        quality_service.validate_text_response(recommendation.reason)

    return NewMenuRecommendationsResponse(recommendations=recommendations)
