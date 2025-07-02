package com.itrail.library.config.redis.domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
/**
 * Информация о сессиях пользователей
 * 
 */
@RedisHash(value = "Session", timeToLive = 900) // 900 -15 минут в секундах
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Session implements Serializable {
    
    @Id         private String        id; 
    @Indexed    private String        sessionId; // ID HTTP сессии
    @Indexed    private SessionType   type;
                private Integer       attempts;
    @Indexed    private String        username;
                private LocalDateTime createdDate = LocalDateTime.now();
    @TimeToLive private Integer       timeToLive;


    public Session( String sessionId, SessionType type, String username, Integer attempts ) {
        this.sessionId = sessionId;
        this.type      = type;
        this.username  = username;
        this.attempts  = attempts;
    }

    public enum SessionType {
        ATTEMPT, BLOCK, AUTHENTICATED
    }
}