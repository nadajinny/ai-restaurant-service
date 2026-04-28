import { computed, ref } from "vue";

const STORAGE_KEY = "restaurant-cart";
const cartItems = ref([]);
const initialized = ref(false);

function readStorage() {
  if (typeof window === "undefined") {
    return [];
  }

  try {
    const raw = window.localStorage.getItem(STORAGE_KEY);
    if (!raw) {
      return [];
    }

    const parsed = JSON.parse(raw);
    return Array.isArray(parsed) ? parsed : [];
  } catch {
    return [];
  }
}

function persist() {
  if (typeof window === "undefined") {
    return;
  }

  window.localStorage.setItem(STORAGE_KEY, JSON.stringify(cartItems.value));
}

function ensureInitialized() {
  if (initialized.value) {
    return;
  }

  cartItems.value = readStorage();
  initialized.value = true;
}

function normalizeMenu(menu) {
  return {
    menuId: menu.menuId,
    name: menu.name,
    price: menu.price,
    category: menu.category,
    imageUrl: menu.imageUrl,
    description: menu.description ?? "",
    cookingTime: menu.cookingTime ?? 0,
    status: menu.status,
    orderable: menu.orderable,
  };
}

export function useCart() {
  ensureInitialized();

  const totalPrice = computed(() =>
    cartItems.value.reduce((sum, item) => sum + item.price * item.quantity, 0),
  );

  const totalQuantity = computed(() =>
    cartItems.value.reduce((sum, item) => sum + item.quantity, 0),
  );

  function addItem(menu) {
    const normalized = normalizeMenu(menu);
    const existing = cartItems.value.find((item) => item.menuId === normalized.menuId);

    if (existing) {
      existing.quantity += 1;
      existing.price = normalized.price;
      existing.status = normalized.status;
      existing.orderable = normalized.orderable;
      existing.imageUrl = normalized.imageUrl;
      existing.cookingTime = normalized.cookingTime;
      existing.description = normalized.description;
      persist();
      return;
    }

    cartItems.value.push({
      ...normalized,
      quantity: 1,
    });
    persist();
  }

  function updateQuantity(menuId, quantity) {
    const target = cartItems.value.find((item) => item.menuId === menuId);
    if (!target) {
      return;
    }

    if (quantity <= 0) {
      removeItem(menuId);
      return;
    }

    target.quantity = quantity;
    persist();
  }

  function removeItem(menuId) {
    cartItems.value = cartItems.value.filter((item) => item.menuId !== menuId);
    persist();
  }

  function clearCart() {
    cartItems.value = [];
    persist();
  }

  function syncMenuStatuses(menus) {
    const menuMap = new Map((menus ?? []).map((menu) => [menu.menuId, menu]));

    cartItems.value = cartItems.value.map((item) => {
      const latest = menuMap.get(item.menuId);

      if (!latest) {
        return {
          ...item,
          status: "HIDDEN",
          orderable: false,
        };
      }

      return {
        ...item,
        ...normalizeMenu(latest),
        quantity: item.quantity,
      };
    });

    persist();
  }

  return {
    cartItems,
    totalPrice,
    totalQuantity,
    addItem,
    updateQuantity,
    removeItem,
    clearCart,
    syncMenuStatuses,
  };
}
