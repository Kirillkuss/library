package com.itrail.library.config.redis;

import java.time.Duration;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableCaching
public class CachingConfig {

    public RedisCacheConfiguration getTimeOut( Integer time ){
        return RedisCacheConfiguration.defaultCacheConfig()
                                    //.computePrefixWith(cacheName -> cacheName + ":" + UUID.randomUUID().toString() )
                                    .entryTtl(Duration.ofSeconds( time ));
    }

    @Bean("libKeyGenerator")
    public KeyGenerator keyGenerator() {
        return new LibKeyGenerator();
    }

    @Bean("SimpleKey")
    public KeyGenerator libSimpleKeyGenerator() {
        return new LibSimpleKeyGenerator();
    }

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        return (builder) -> builder.withCacheConfiguration( "records", getTimeOut( 8 ) )
                                   .withCacheConfiguration( "books",getTimeOut( 8 ))
                                   .withCacheConfiguration( "cards",getTimeOut( 8 ))
                                   .withCacheConfiguration( "users", getTimeOut( 5 ));
    }

    
}
