from pydantic import BaseModel, Field


class ReviewGenerateRequest(BaseModel):
    menuId: int
    keywords: list[str] = Field(..., min_length=1)


class ReviewGenerateResponse(BaseModel):
    menuId: int
    reviewDraft: str
    aiGenerated: bool = True


class ReviewSummaryResponse(BaseModel):
    menuId: int
    summary: str
