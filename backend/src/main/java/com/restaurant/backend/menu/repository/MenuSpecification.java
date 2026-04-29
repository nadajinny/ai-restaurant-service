package com.restaurant.backend.menu.repository;

import com.restaurant.backend.menu.domain.Menu;
import com.restaurant.backend.menu.domain.MenuStatus;
import com.restaurant.backend.menu.dto.MenuSearchRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public final class MenuSpecification {

    private MenuSpecification() {
    }

    public static Specification<Menu> bySearchRequest(MenuSearchRequest request) {
        return excludeHidden()
                .and(hasCategory(request.getCategory()))
                .and(hasMinPrice(request.getMinPrice()))
                .and(hasMaxPrice(request.getMaxPrice()))
                .and(hasStatus(request.getStatus()));
    }

    private static Specification<Menu> excludeHidden() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.notEqual(root.get("status"), MenuStatus.HIDDEN);
    }

    private static Specification<Menu> hasCategory(String category) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(category)) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(root.get("category"), category);
        };
    }

    private static Specification<Menu> hasMinPrice(Integer minPrice) {
        return (root, query, criteriaBuilder) -> {
            if (minPrice == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice);
        };
    }

    private static Specification<Menu> hasMaxPrice(Integer maxPrice) {
        return (root, query, criteriaBuilder) -> {
            if (maxPrice == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice);
        };
    }

    private static Specification<Menu> hasStatus(MenuStatus status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(root.get("status"), status);
        };
    }
}
