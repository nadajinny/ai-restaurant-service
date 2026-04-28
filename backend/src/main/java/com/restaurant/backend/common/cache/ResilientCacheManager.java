package com.restaurant.backend.common.cache;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

public class ResilientCacheManager implements CacheManager {

    private final CacheManager primaryCacheManager;
    private final CacheManager fallbackCacheManager;
    private final ConcurrentMap<String, Cache> caches = new ConcurrentHashMap<>();

    public ResilientCacheManager(CacheManager primaryCacheManager, CacheManager fallbackCacheManager) {
        this.primaryCacheManager = primaryCacheManager;
        this.fallbackCacheManager = fallbackCacheManager;
    }

    @Override
    public Cache getCache(String name) {
        return caches.computeIfAbsent(name, this::createCache);
    }

    @Override
    public Set<String> getCacheNames() {
        Set<String> names = new LinkedHashSet<>(primaryCacheManager.getCacheNames());
        names.addAll(fallbackCacheManager.getCacheNames());
        return names;
    }

    private Cache createCache(String name) {
        Cache primary = primaryCacheManager.getCache(name);
        Cache fallback = fallbackCacheManager.getCache(name);

        if (primary == null) {
            return fallback;
        }

        if (fallback == null) {
            return primary;
        }

        return new ResilientCache(name, primary, fallback);
    }
}
