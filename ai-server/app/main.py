from fastapi import FastAPI

from app.api.routes.admin_ai import router as admin_ai_router
from app.api.routes.ai import router as ai_router
from app.core.config import get_settings

settings = get_settings()

app = FastAPI(
    title=settings.app_name,
    version=settings.app_version,
)

app.include_router(ai_router)
app.include_router(admin_ai_router)


@app.get("/health")
async def health() -> dict[str, str]:
    return {"status": "ok"}
