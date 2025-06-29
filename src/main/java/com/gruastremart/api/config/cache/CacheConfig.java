package com.gruastremart.api.config.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.TimeUnit;

import static com.gruastremart.api.utils.constants.Constants.OPERATOR_LOCATIONS_CACHE;

@Configuration
@EnableCaching
public class CacheConfig {

    @Primary
    @Bean(name = "operatorLocationsCacheManager")
    public CacheManager operatorLocationsCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(OPERATOR_LOCATIONS_CACHE);
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES) // Las localizaciones expiran en 5 minutos
                .maximumSize(1000)); // Máximo 1000 operadores
        return cacheManager;
    }
}
