package com.restaurant.backend.analytics.service;

import com.restaurant.backend.analytics.dto.DashboardResponse;
import com.restaurant.backend.analytics.dto.HourlyOrderResponse;
import com.restaurant.backend.analytics.dto.MenuPerformanceResponse;
import com.restaurant.backend.analytics.dto.PopularMenuResponse;
import com.restaurant.backend.analytics.dto.RecentReviewResponse;
import com.restaurant.backend.analytics.dto.SalesAnalyticsResponse;
import com.restaurant.backend.analytics.dto.SoldOutMenuResponse;
import com.restaurant.backend.common.cache.CacheNames;
import com.restaurant.backend.common.exception.BusinessException;
import com.restaurant.backend.common.exception.ErrorCode;
import com.restaurant.backend.menu.domain.Menu;
import com.restaurant.backend.menu.domain.MenuStatus;
import com.restaurant.backend.menu.repository.MenuRepository;
import com.restaurant.backend.order.domain.Order;
import com.restaurant.backend.order.domain.OrderItem;
import com.restaurant.backend.order.domain.OrderStatus;
import com.restaurant.backend.order.repository.OrderRepository;
import com.restaurant.backend.review.domain.Review;
import com.restaurant.backend.review.repository.ReviewRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AnalyticsService {

    private final OrderRepository orderRepository;
    private final MenuRepository menuRepository;
    private final ReviewRepository reviewRepository;

    public AnalyticsService(
            OrderRepository orderRepository,
            MenuRepository menuRepository,
            ReviewRepository reviewRepository
    ) {
        this.orderRepository = orderRepository;
        this.menuRepository = menuRepository;
        this.reviewRepository = reviewRepository;
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CacheNames.ADMIN_DASHBOARD)
    public DashboardResponse getDashboard() {
        LocalDate today = LocalDate.now();
        List<Order> orders = orderRepository.findAll();

        long todayOrderCount = orders.stream()
                .filter(order -> order.getCreatedAt() != null && order.getCreatedAt().toLocalDate().isEqual(today))
                .count();

        int todaySales = orders.stream()
                .filter(order -> order.getStatus() == OrderStatus.COMPLETED)
                .filter(order -> order.getCreatedAt() != null && order.getCreatedAt().toLocalDate().isEqual(today))
                .mapToInt(Order::getTotalPrice)
                .sum();

        return new DashboardResponse(
                todayOrderCount,
                todaySales,
                getPopularMenus(),
                getHourlyOrders(),
                getSoldOutMenus(),
                getRecentReviews()
        );
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CacheNames.SALES_ANALYTICS)
    public SalesAnalyticsResponse getSalesAnalytics() {
        return getSalesAnalytics(null, null);
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CacheNames.SALES_ANALYTICS, key = "#startDate + ':' + #endDate")
    public SalesAnalyticsResponse getSalesAnalytics(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "시작일은 종료일보다 늦을 수 없습니다.");
        }

        List<Order> completedOrders = orderRepository.findAll().stream()
                .filter(order -> order.getStatus() == OrderStatus.COMPLETED)
                .filter(order -> isWithinRange(order, startDate, endDate))
                .toList();

        int totalSales = completedOrders.stream()
                .mapToInt(Order::getTotalPrice)
                .sum();
        long completedOrderCount = completedOrders.size();
        int averageOrderAmount = completedOrderCount == 0 ? 0 : totalSales / (int) completedOrderCount;

        return new SalesAnalyticsResponse(completedOrderCount, totalSales, averageOrderAmount);
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CacheNames.POPULAR_MENUS)
    public List<PopularMenuResponse> getPopularMenus() {
        return buildMenuStats().values().stream()
                .sorted(Comparator.comparingInt(MenuStat::soldQuantity).reversed()
                        .thenComparing(Comparator.comparingInt(MenuStat::salesAmount).reversed())
                        .thenComparing(MenuStat::menuId))
                .map(stat -> new PopularMenuResponse(
                        stat.menuId(),
                        stat.menuName(),
                        stat.category(),
                        stat.soldQuantity(),
                        stat.salesAmount()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CacheNames.MENU_PERFORMANCE)
    public List<MenuPerformanceResponse> getMenuPerformance() {
        return buildMenuStats().values().stream()
                .sorted(Comparator.comparing(MenuStat::menuId))
                .map(stat -> new MenuPerformanceResponse(
                        stat.menuId(),
                        stat.menuName(),
                        stat.category(),
                        stat.soldQuantity(),
                        stat.salesAmount()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CacheNames.HOURLY_ORDERS)
    public List<HourlyOrderResponse> getHourlyOrders() {
        long[] hourCounts = new long[24];

        for (Order order : orderRepository.findAll()) {
            if (order.getCreatedAt() != null) {
                hourCounts[order.getCreatedAt().getHour()]++;
            }
        }

        List<HourlyOrderResponse> responses = new ArrayList<>();
        for (int hour = 0; hour < 24; hour++) {
            responses.add(new HourlyOrderResponse(hour, hourCounts[hour]));
        }
        return responses;
    }

    private List<SoldOutMenuResponse> getSoldOutMenus() {
        return menuRepository.findAll().stream()
                .filter(menu -> menu.getStatus() == MenuStatus.SOLD_OUT)
                .sorted(Comparator.comparing(Menu::getId))
                .map(menu -> new SoldOutMenuResponse(menu.getId(), menu.getName(), menu.getCategory()))
                .toList();
    }

    private List<RecentReviewResponse> getRecentReviews() {
        return reviewRepository.findAllByOrderByCreatedAtDescIdDesc().stream()
                .limit(5)
                .map(review -> new RecentReviewResponse(
                        review.getId(),
                        review.getMenu().getId(),
                        review.getMenu().getName(),
                        review.getRating(),
                        review.getContent(),
                        review.getStatus(),
                        review.getCreatedAt()
                ))
                .toList();
    }

    private Map<Long, MenuStat> buildMenuStats() {
        Map<Long, MenuStat> stats = new LinkedHashMap<>();

        for (Menu menu : menuRepository.findAll()) {
            stats.put(menu.getId(), new MenuStat(menu.getId(), menu.getName(), menu.getCategory(), 0, 0));
        }

        for (Order order : orderRepository.findAll()) {
            if (order.getStatus() != OrderStatus.COMPLETED) {
                continue;
            }

            for (OrderItem orderItem : order.getOrderItems()) {
                MenuStat current = stats.get(orderItem.getMenu().getId());
                if (current == null) {
                    continue;
                }

                stats.put(orderItem.getMenu().getId(), new MenuStat(
                        current.menuId(),
                        current.menuName(),
                        current.category(),
                        current.soldQuantity() + orderItem.getQuantity(),
                        current.salesAmount() + (orderItem.getQuantity() * orderItem.getItemPrice())
                ));
            }
        }

        return stats;
    }

    private record MenuStat(
            Long menuId,
            String menuName,
            String category,
            int soldQuantity,
            int salesAmount
    ) {
    }

    private boolean isWithinRange(Order order, LocalDate startDate, LocalDate endDate) {
        if (order.getCreatedAt() == null) {
            return false;
        }

        LocalDate orderedDate = order.getCreatedAt().toLocalDate();

        if (startDate != null && orderedDate.isBefore(startDate)) {
            return false;
        }

        if (endDate != null && orderedDate.isAfter(endDate)) {
            return false;
        }

        return true;
    }
}
