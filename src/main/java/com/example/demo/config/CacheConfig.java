package com.example.demo.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();

        cacheManager.registerCustomCache("weatherCache", Caffeine.newBuilder()
                .maximumSize(10)
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .build());

        cacheManager.registerCustomCache("astronomyCache", Caffeine.newBuilder()
                .maximumSize(10)
                .expireAfterWrite(1, TimeUnit.HOURS)
                .build());

        cacheManager.registerCustomCache("waterCache", Caffeine.newBuilder()
                .maximumSize(10)
                .expireAfterWrite(1, TimeUnit.HOURS)
                .build());

        return cacheManager;
    }
}