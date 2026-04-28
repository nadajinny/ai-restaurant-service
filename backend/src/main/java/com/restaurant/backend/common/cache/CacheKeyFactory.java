package com.restaurant.backend.common.cache;

import com.restaurant.backend.menu.dto.MenuSearchRequest;
import com.restaurant.backend.menu.dto.MenuSortType;

public final class CacheKeyFactory {

    private CacheKeyFactory() {
    }

    public static String menuSearch(MenuSearchRequest request) {
        MenuSortType sortType = request.getSort() == null ? MenuSortType.LATEST : request.getSort();

        return "category=" + normalize(request.getCategory())
                + "|minPrice=" + normalize(request.getMinPrice())
                + "|maxPrice=" + normalize(request.getMaxPrice())
                + "|status=" + normalize(request.getStatus())
                + "|sort=" + sortType.name();
    }

    private static String normalize(Object value) {
        return value == null ? "ALL" : String.valueOf(value);
    }
}
