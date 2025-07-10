package com.itrail.library.config.redis.service;

import java.util.Iterator;
import java.util.Optional;
import java.util.stream.StreamSupport;
import org.springframework.stereotype.Service;
import com.itrail.library.config.redis.domain.Session;
import com.itrail.library.config.redis.domain.Session.SessionType;
import com.itrail.library.config.redis.repository.SessionRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
/**
 * Сервис для работы с сессиями
 */
@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;
    
    /*
     * Удаление текущей сессии
     * ident - sessionId; // ID HTTP сессии
     * HttpServletRequest - запрос
     */
    public void deleteCurrentSession( HttpServletRequest httpServletRequest) throws IllegalAccessException{
        HttpSession currentSession = httpServletRequest.getSession(false);
        if (currentSession != null) {
            String currentSessionId = currentSession.getId();
            String username         = ( String ) currentSession.getAttribute("AUTH_USERNAME");
            if ( username != null ) {
                 Optional<Session> deleteSession = sessionRepository.findByUsernameAndType( username, SessionType.AUTHENTICATED );
                if ( deleteSession.isPresent() ) {
                    sessionRepository.deleteBySessionId( currentSessionId );
                    currentSession.invalidate();
                }
            }
        }else{
            throw new IllegalAccessException("Нет прав!");
        }
    }
    /**
     * Получение списка сессий
     * @return Iterator Session 
     */
    public Iterator<Session> getSessions( ){
        return StreamSupport.stream( sessionRepository.findAll().spliterator(), false)
                                                  .filter( f ->  f != null )
                                                  .iterator();
    }

}
