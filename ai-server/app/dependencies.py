from functools import lru_cache

from app.clients.backend_menu_client import BackendMenuClient
from app.core.config import get_settings
from app.services.ai_mock_service import MockAiService
from app.services.menu_catalog_service import MenuCatalogService
from app.services.policy_service import PolicyValidationService
from app.services.quality_service import QualityValidationService


@lru_cache
def get_policy_service() -> PolicyValidationService:
    return PolicyValidationService()


@lru_cache
def get_quality_service() -> QualityValidationService:
    return QualityValidationService()


@lru_cache
def get_mock_ai_service() -> MockAiService:
    return MockAiService()


@lru_cache
def get_backend_menu_client() -> BackendMenuClient:
    settings = get_settings()
    return BackendMenuClient(
        base_url=settings.backend_base_url,
        connect_timeout=settings.backend_connect_timeout,
        read_timeout=settings.backend_read_timeout,
    )


@lru_cache
def get_menu_catalog_service() -> MenuCatalogService:
    return MenuCatalogService(get_backend_menu_client())
