package com.socia1ca3t;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import com.socia1ca3t.timepillbackup.properties.TimepillConfig;
import org.checkerframework.checker.index.qual.NonNegative;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.List;
import java.util.concurrent.TimeUnit;


@Configuration
public class CacheConfig {

    @Primary
    @Bean("backupCacheManager")
    public CacheManager backupCacheManager() {

        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder());

        return cacheManager;
    }


    /**
     * 请求胶囊接口的缓存管理
     */
    @Bean("httpApiCacheManager")
    public CacheManager httpApiCacheManager(TimepillConfig config) {

        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfter(getHttpApiCacheExpiry(config.httpCacheMinutes()))
                // 缓存的最大条数
                .maximumSize(1000));

        return cacheManager;
    }

    private Expiry<Object, Object> getHttpApiCacheExpiry(int minutes) {

        return new Expiry<>() {

            @Override
            public long expireAfterCreate(Object key, Object value, long currentTime) {

                // 接口响应的数据为空，则不做缓存
                if (value instanceof List<?> list && list.isEmpty()) {
                    return 0;
                }
                // 接口响应的数据不为空，过期时间为配置的值
                return TimeUnit.MINUTES.toNanos(minutes);
            }

            @Override
            public long expireAfterUpdate(Object key, Object value, long currentTime, @NonNegative long currentDuration) {

                return currentDuration;
            }

            @Override
            public long expireAfterRead(Object key, Object value, long currentTime, @NonNegative long currentDuration) {

                return currentDuration;
            }
        };
    }

}
