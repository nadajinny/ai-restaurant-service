from pydantic import BaseModel, Field

from app.schemas.common import MenuRecommendation


class RecommendRequest(BaseModel):
    message: str = Field(..., min_length=1, max_length=500)


class RecommendResponse(BaseModel):
    recommendations: list[MenuRecommendation]


class PersonalizedRecommendationResponse(BaseModel):
    userId: int | None = None
    recommendations: list[MenuRecommendation]


class EmotionRecommendRequest(BaseModel):
    emotion: str = Field(..., min_length=1, max_length=100)
    context: str | None = Field(default=None, max_length=500)


class EmotionRecommendResponse(BaseModel):
    recommendations: list[MenuRecommendation]
