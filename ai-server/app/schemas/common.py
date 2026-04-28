from pydantic import BaseModel


class MenuRecommendation(BaseModel):
    menuId: int
    name: str
    reason: str
