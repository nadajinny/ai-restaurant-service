from app.clients.backend_menu_client import BackendMenuClient, MenuCatalogItem


class MenuCatalogService:
    def __init__(self, backend_menu_client: BackendMenuClient) -> None:
        self._backend_menu_client = backend_menu_client

    async def get_recommendable_menus(self) -> list[MenuCatalogItem]:
        try:
            menus = await self._backend_menu_client.fetch_available_menus()
            if menus:
                return [menu for menu in menus if menu.status == "AVAILABLE"]
        except Exception:
            pass

        return [
            MenuCatalogItem(menu_id=1, name="김치찌개", category="KOREAN", status="AVAILABLE"),
            MenuCatalogItem(menu_id=2, name="제육볶음", category="KOREAN", status="AVAILABLE"),
            MenuCatalogItem(menu_id=3, name="돈까스", category="JAPANESE", status="AVAILABLE"),
            MenuCatalogItem(menu_id=4, name="새우볶음밥", category="CHINESE", status="AVAILABLE"),
        ]
