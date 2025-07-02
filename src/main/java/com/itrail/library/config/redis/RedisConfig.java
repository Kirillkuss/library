package com.itrail.library.config.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisKeyExpiredEvent;
import org.springframework.data.redis.core.RedisKeyValueAdapter.EnableKeyspaceEvents;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import com.itrail.library.config.redis.domain.Session;
import com.itrail.library.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@ComponentScan("com.itrail.library.config.redis")
@PropertySource(value = { "classpath:redis.properties" })
@EnableRedisRepositories( basePackages = "com.itrail.library.config.redis.repository", 
                          enableKeyspaceEvents = EnableKeyspaceEvents.ON_STARTUP )
@RequiredArgsConstructor
public class RedisConfig {

    private final AuthService authService;

    @Bean
    public RedisTemplate<String, Session> redisTemplateSession(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Session> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(Session.class));
        return template;
    } 

    @Bean
    public RedisTemplate<String, Object> redisTemplateObject(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
        return template;
    }

    @EventListener
    public void handleRedisKeyExpiredEventSession( RedisKeyExpiredEvent<Session> event ) {
        assert event != null;
        if (event.getValue() instanceof Session) {
                Session expiredSession = (Session) event.getValue();
                    if (expiredSession != null) {
                        String sessionId = expiredSession.getSessionId();
                        log.info("Session expired: {}", expiredSession);
                        authService.logout( sessionId ); 
                    }
        }else{
            log.info("Cache with key = {} has expired", new String(  event.getId()));
        }
    }
       
    
    
}
