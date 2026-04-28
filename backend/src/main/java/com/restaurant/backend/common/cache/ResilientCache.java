package com.restaurant.backend.common.cache;

import java.util.concurrent.Callable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;

public class ResilientCache implements Cache {

    private static final Logger log = LoggerFactory.getLogger(ResilientCache.class);

    private final String name;
    private final Cache primary;
    private final Cache fallback;

    public ResilientCache(String name, Cache primary, Cache fallback) {
        this.name = name;
        this.primary = primary;
        this.fallback = fallback;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Object getNativeCache() {
        return primary.getNativeCache();
    }

    @Override
    public ValueWrapper get(Object key) {
        try {
            return primary.get(key);
        } catch (RuntimeException exception) {
            log.warn("Primary cache GET failed. cache={}, key={}", name, key, exception);
            return fallback.get(key);
        }
    }

    @Override
    public <T> T get(Object key, Class<T> type) {
        try {
            return primary.get(key, type);
        } catch (RuntimeException exception) {
            log.warn("Primary cache GET(type) failed. cache={}, key={}", name, key, exception);
            return fallback.get(key, type);
        }
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        try {
            return primary.get(key, valueLoader);
        } catch (RuntimeException exception) {
            log.warn("Primary cache GET(loader) failed. cache={}, key={}", name, key, exception);
            return fallback.get(key, valueLoader);
        }
    }

    @Override
    public void put(Object key, Object value) {
        try {
            primary.put(key, value);
        } catch (RuntimeException exception) {
            log.warn("Primary cache PUT failed. cache={}, key={}", name, key, exception);
        }
        fallback.put(key, value);
    }

    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        try {
            ValueWrapper primaryResult = primary.putIfAbsent(key, value);
            fallback.putIfAbsent(key, value);
            return primaryResult;
        } catch (RuntimeException exception) {
            log.warn("Primary cache PUT_IF_ABSENT failed. cache={}, key={}", name, key, exception);
            return fallback.putIfAbsent(key, value);
        }
    }

    @Override
    public void evict(Object key) {
        try {
            primary.evict(key);
        } catch (RuntimeException exception) {
            log.warn("Primary cache EVICT failed. cache={}, key={}", name, key, exception);
        }
        fallback.evict(key);
    }

    @Override
    public boolean evictIfPresent(Object key) {
        boolean fallbackResult = fallback.evictIfPresent(key);

        try {
            return primary.evictIfPresent(key) || fallbackResult;
        } catch (RuntimeException exception) {
            log.warn("Primary cache EVICT_IF_PRESENT failed. cache={}, key={}", name, key, exception);
            return fallbackResult;
        }
    }

    @Override
    public void clear() {
        try {
            primary.clear();
        } catch (RuntimeException exception) {
            log.warn("Primary cache CLEAR failed. cache={}", name, exception);
        }
        fallback.clear();
    }

    @Override
    public boolean invalidate() {
        boolean fallbackResult = fallback.invalidate();

        try {
            return primary.invalidate() || fallbackResult;
        } catch (RuntimeException exception) {
            log.warn("Primary cache INVALIDATE failed. cache={}", name, exception);
            return fallbackResult;
        }
    }
}
