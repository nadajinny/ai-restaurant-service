package com.restaurant.backend.common.cache;

import java.util.List;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Service
public class CacheInvalidationService {

    private final CacheManager cacheManager;

    public CacheInvalidationService(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public void evictMenuCaches(Long menuId) {
        evictAll(CacheNames.MENUS);
        evict(CacheNames.MENU_DETAILS, menuId);
        evictAnalyticsCaches();
    }

    public void evictReviewSummary(Long menuId) {
        evict(CacheNames.REVIEW_SUMMARIES, menuId);
    }

    public void evictUserPersonalizedRecommendations(Long userId) {
        if (userId == null) {
            return;
        }

        evict(CacheNames.PERSONALIZED_RECOMMENDATIONS, userId);
    }

    public void evictDashboardCache() {
        evictAll(CacheNames.ADMIN_DASHBOARD);
    }

    public void evictAnalyticsCaches() {
        evictAll(CacheNames.ANALYTICS);
    }

    public void evictReviewRelatedCaches(Long menuId) {
        evictReviewSummary(menuId);
        evictDashboardCache();
    }

    public void evictMenuAndAnalyticsCaches(Long menuId) {
        evictMenuCaches(menuId);
    }

    private void evictAll(List<String> cacheNames) {
        cacheNames.forEach(this::evictAll);
    }

    private void evictAll(String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.clear();
        }
    }

    private void evict(String cacheName, Object key) {
        if (key == null) {
            return;
        }

        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.evict(key);
        }
    }
}
