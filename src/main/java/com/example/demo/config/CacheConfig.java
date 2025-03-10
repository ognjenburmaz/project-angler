package com.example.demo.config;

import com.example.demo.service.AstronomyService;
import com.example.demo.service.WaterService;
import com.example.demo.service.WeatherService;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    private final WeatherService weatherService;
    private final AstronomyService astronomyService;
    private final WaterService waterService;

    @Autowired
    public CacheConfig(WeatherService weatherService, AstronomyService astronomyService, WaterService waterService) {
        this.weatherService = weatherService;
        this.astronomyService = astronomyService;
        this.waterService = waterService;
    }

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

        cacheManager.registerCustomCache("weatherCache", Caffeine.newBuilder()
                .maximumSize(10)
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .refreshAfterWrite(4, TimeUnit.MINUTES)
                .executor(executor)
                .build(key -> weatherService.getWeatherData((String) key)));

        cacheManager.registerCustomCache("astronomyCache", Caffeine.newBuilder()
                .maximumSize(10)
                .expireAfterWrite(1, TimeUnit.HOURS)
                .refreshAfterWrite(58, TimeUnit.MINUTES)
                .executor(executor)
                .build(key -> astronomyService.getAstronomyData((String) key)));

        cacheManager.registerCustomCache("waterCache", Caffeine.newBuilder()
                .maximumSize(10)
                .expireAfterWrite(1, TimeUnit.HOURS)
                .refreshAfterWrite(59, TimeUnit.MINUTES)
                .executor(executor)
                .build(key -> waterService.getWaterData((String) key)));

        return cacheManager;
    }
}