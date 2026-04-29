from dataclasses import dataclass

import httpx


@dataclass(frozen=True)
class MenuCatalogItem:
    menu_id: int
    name: str
    category: str
    status: str


class BackendMenuClient:
    def __init__(self, base_url: str, connect_timeout: float, read_timeout: float) -> None:
        self._base_url = base_url.rstrip("/")
        self._timeout = httpx.Timeout(connect=connect_timeout, read=read_timeout, write=read_timeout, pool=read_timeout)

    async def fetch_available_menus(self) -> list[MenuCatalogItem]:
        url = f"{self._base_url}/menus"
        async with httpx.AsyncClient(timeout=self._timeout) as client:
            response = await client.get(url)
            response.raise_for_status()
            payload = response.json()

        items: list[MenuCatalogItem] = []
        for raw in payload.get("data", []):
            items.append(
                MenuCatalogItem(
                    menu_id=raw["menuId"],
                    name=raw["name"],
                    category=raw["category"],
                    status=raw["status"],
                )
            )
        return items
