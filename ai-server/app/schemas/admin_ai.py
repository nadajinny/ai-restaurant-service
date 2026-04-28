from pydantic import BaseModel


class NewMenuRecommendation(BaseModel):
    name: str
    category: str
    reason: str


class NewMenuRecommendationsResponse(BaseModel):
    recommendations: list[NewMenuRecommendation]
