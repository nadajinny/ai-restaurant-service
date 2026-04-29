package com.restaurant.backend.menu.dto;

import com.restaurant.backend.menu.domain.MenuStatus;
import jakarta.validation.constraints.Min;

public class MenuSearchRequest {

    private String category;

    @Min(value = 0, message = "minPrice는 0 이상이어야 합니다.")
    private Integer minPrice;

    @Min(value = 0, message = "maxPrice는 0 이상이어야 합니다.")
    private Integer maxPrice;

    private MenuStatus status;
    private MenuSortType sort;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(Integer minPrice) {
        this.minPrice = minPrice;
    }

    public Integer getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Integer maxPrice) {
        this.maxPrice = maxPrice;
    }

    public MenuStatus getStatus() {
        return status;
    }

    public void setStatus(MenuStatus status) {
        this.status = status;
    }

    public MenuSortType getSort() {
        return sort;
    }

    public void setSort(MenuSortType sort) {
        this.sort = sort;
    }
}
