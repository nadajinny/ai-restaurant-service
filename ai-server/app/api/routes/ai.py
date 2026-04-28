from fastapi import APIRouter, Depends, HTTPException, Query, status

from app.dependencies import (
    get_menu_catalog_service,
    get_mock_ai_service,
    get_policy_service,
    get_quality_service,
)
from app.schemas.recommend import (
    EmotionRecommendRequest,
    EmotionRecommendResponse,
    PersonalizedRecommendationResponse,
    RecommendRequest,
    RecommendResponse,
)
from app.schemas.review import (
    ReviewGenerateRequest,
    ReviewGenerateResponse,
    ReviewSummaryResponse,
)
from app.services.ai_mock_service import MockAiService
from app.services.menu_catalog_service import MenuCatalogService
from app.services.policy_service import PolicyValidationService
from app.services.quality_service import QualityValidationService

router = APIRouter(tags=["ai"])


@router.post("/ai/recommend", response_model=RecommendResponse)
async def recommend(
    request: RecommendRequest,
    policy_service: PolicyValidationService = Depends(get_policy_service),
    quality_service: QualityValidationService = Depends(get_quality_service),
    menu_catalog_service: MenuCatalogService = Depends(get_menu_catalog_service),
    mock_ai_service: MockAiService = Depends(get_mock_ai_service),
) -> RecommendResponse:
    policy_service.validate_input_text(request.message)
    menus = await menu_catalog_service.get_recommendable_menus()
    recommendations = mock_ai_service.recommend_by_message(request.message, menus)
    valid_recommendations = quality_service.validate_recommendations(
        recommendations,
        {menu.menu_id for menu in menus},
    )
    return RecommendResponse(recommendations=valid_recommendations)


@router.get("/ai/personalized-recommendations", response_model=PersonalizedRecommendationResponse)
async def personalized_recommendations(
    userId: int | None = Query(default=None),
    menu_catalog_service: MenuCatalogService = Depends(get_menu_catalog_service),
    quality_service: QualityValidationService = Depends(get_quality_service),
    mock_ai_service: MockAiService = Depends(get_mock_ai_service),
) -> PersonalizedRecommendationResponse:
    menus = await menu_catalog_service.get_recommendable_menus()
    recommendations = mock_ai_service.personalized_recommendations(userId, menus)
    valid_recommendations = quality_service.validate_recommendations(
        recommendations,
        {menu.menu_id for menu in menus},
    )
    return PersonalizedRecommendationResponse(userId=userId, recommendations=valid_recommendations)


@router.post("/ai/emotion-recommend", response_model=EmotionRecommendResponse)
async def emotion_recommend(
    request: EmotionRecommendRequest,
    policy_service: PolicyValidationService = Depends(get_policy_service),
    quality_service: QualityValidationService = Depends(get_quality_service),
    menu_catalog_service: MenuCatalogService = Depends(get_menu_catalog_service),
    mock_ai_service: MockAiService = Depends(get_mock_ai_service),
) -> EmotionRecommendResponse:
    policy_service.validate_input_text(request.emotion)
    if request.context:
        policy_service.validate_input_text(request.context)

    menus = await menu_catalog_service.get_recommendable_menus()
    recommendations = mock_ai_service.recommend_by_emotion(request.emotion, request.context, menus)
    valid_recommendations = quality_service.validate_recommendations(
        recommendations,
        {menu.menu_id for menu in menus},
    )
    return EmotionRecommendResponse(recommendations=valid_recommendations)


@router.post("/ai/review-generate", response_model=ReviewGenerateResponse)
async def review_generate(
    request: ReviewGenerateRequest,
    policy_service: PolicyValidationService = Depends(get_policy_service),
    quality_service: QualityValidationService = Depends(get_quality_service),
    menu_catalog_service: MenuCatalogService = Depends(get_menu_catalog_service),
    mock_ai_service: MockAiService = Depends(get_mock_ai_service),
) -> ReviewGenerateResponse:
    for keyword in request.keywords:
        policy_service.validate_input_text(keyword)

    menus = await menu_catalog_service.get_recommendable_menus()
    menu_map = {menu.menu_id: menu for menu in menus}
    menu = menu_map.get(request.menuId)
    if menu is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="지원하지 않는 menuId입니다.",
        )

    draft = mock_ai_service.generate_review_draft(menu.name, request.keywords)
    policy_service.validate_output_text(draft)
    valid_draft = quality_service.validate_text_response(draft)
    return ReviewGenerateResponse(menuId=request.menuId, reviewDraft=valid_draft)


@router.get("/ai/menus/{menu_id}/review-summary", response_model=ReviewSummaryResponse)
async def review_summary(
    menu_id: int,
    policy_service: PolicyValidationService = Depends(get_policy_service),
    quality_service: QualityValidationService = Depends(get_quality_service),
    menu_catalog_service: MenuCatalogService = Depends(get_menu_catalog_service),
    mock_ai_service: MockAiService = Depends(get_mock_ai_service),
) -> ReviewSummaryResponse:
    menus = await menu_catalog_service.get_recommendable_menus()
    menu_map = {menu.menu_id: menu for menu in menus}
    menu = menu_map.get(menu_id)
    if menu is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="지원하지 않는 menuId입니다.",
        )

    summary = mock_ai_service.summarize_reviews(menu.name)
    policy_service.validate_output_text(summary)
    valid_summary = quality_service.validate_text_response(summary)
    return ReviewSummaryResponse(menuId=menu_id, summary=valid_summary)
