package com.socia1ca3t.timepillbackup.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

public class BackupCacheUtil {

    private static final Logger logger = LoggerFactory.getLogger(BackupCacheUtil.class);

    private static final CacheManager cacheManager = SpringContextUtil.getBean("backupCacheManager");

    public static void put(String cacheName, Object key, Object value) {

        logger.info("{}增加键值对{}:{}", cacheName, key, value);
        cacheManager.getCache(cacheName).put(key, value);
    }


    public static <T> T get(String cacheName, Object key) {

        Cache.ValueWrapper wrapper = cacheManager.getCache(cacheName).get(key);

        if (wrapper == null) {
            return null;
        }

        T value = (T) wrapper.get();

        logger.info("{}获取键值对{}:{}", cacheName, key, value);

        return value;
    }


    public static void evictIfPresent(String cacheName, Object key) {

        logger.info("{}存在则清除键值对{}", cacheName, key);

        cacheManager.getCache(cacheName).evictIfPresent(key);
    }
}
