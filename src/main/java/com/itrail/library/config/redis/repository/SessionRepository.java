package com.itrail.library.config.redis.repository;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.itrail.library.config.redis.domain.Session;
import com.itrail.library.config.redis.domain.Session.SessionType;

@Repository
public interface SessionRepository extends CrudRepository<Session, String> {

    Optional<Session> findBySessionIdAndType( String sessionId, SessionType type );
    Optional<Session> findByUsernameAndType( String username, SessionType type );
    Optional<Session> findBySessionId( String sessionId );

    default void deleteBySessionId( String sessionId ){
        findBySessionId(sessionId).ifPresent(this::delete);
    }

    default void deleteBySessionIdAndType( String sessionId, SessionType type ){
        findBySessionIdAndType( sessionId, type ).ifPresent( this::delete );
    }

    default void deleteByUsernameAndType( String username, SessionType type ){
        findByUsernameAndType( username, type ).ifPresent( this::delete );
    }

    

}
