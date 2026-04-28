from functools import lru_cache

from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    model_config = SettingsConfigDict(
        env_file=".env",
        env_file_encoding="utf-8",
        extra="ignore",
    )

    app_name: str = "AI Restaurant Service"
    app_version: str = "0.1.0"
    ai_provider: str = "mock"
    openai_api_key: str | None = None
    backend_base_url: str = "http://localhost:8080"
    backend_connect_timeout: float = 3.0
    backend_read_timeout: float = 5.0


@lru_cache
def get_settings() -> Settings:
    return Settings()
