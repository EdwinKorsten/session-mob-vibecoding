package com.evcharging.dynamicpricing.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class AppConfig {

    @Value("${app.bzn:DE-LU}")
    private String bzn;

    @Value("${app.prices.api.base:https://api.energy-charts.info}")
    private String pricesApiBase;

    @Value("${app.timezone:Europe/Amsterdam}")
    private String timezone;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofMinutes(30))
            .maximumSize(100));
        return cacheManager;
    }

    public String getBzn() {
        return bzn;
    }

    public String getPricesApiBase() {
        return pricesApiBase;
    }

    public String getTimezone() {
        return timezone;
    }
}