package com.itrail.library.sequrity.filter;

import java.io.IOException;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.itrail.library.config.redis.domain.Session;
import com.itrail.library.config.redis.domain.Session.SessionType;
import com.itrail.library.config.redis.repository.SessionRepository;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class LibSingleSessionFilter extends OncePerRequestFilter {

    private final SessionRepository sessionRepository;

    @PostConstruct
    public void init(){
        sessionRepository.deleteAll();
    }

    @Override
    protected void doFilterInternal( HttpServletRequest httpServletRequest,
                                     HttpServletResponse httpServletResponse,
                                     FilterChain filterChain) throws ServletException, IOException {
        
        HttpSession currentSession = httpServletRequest.getSession(false);
        if (currentSession != null) {
            
            String currentSessionId = currentSession.getId();
            String username         = (String) currentSession.getAttribute("AUTH_USERNAME");

            if ( username != null ) {
                Optional<Session> activeAuthSession = sessionRepository.findByUsernameAndType( username, SessionType.AUTHENTICATED );
                if (activeAuthSession.isPresent()) {
                    Session authSession = activeAuthSession.get();
                    if (!authSession.getSessionId().equals(currentSessionId)) {
                        log.warn("Multiple sessions detected for user {}. Current: {}, Active: {}",  username, currentSessionId, authSession.getSessionId());
                        // 1. Инвалидируем текущую сессию (новую)
                        currentSession.invalidate();
                        httpServletResponse.sendRedirect("/login?error=multiple_session");
                        return;
                        // 2. Инвалидируем старую сессию и разрешаем новую
                        // sessionRepository.delete(authSession);
                        // authSession.setSessionId(currentSessionId);
                        // sessionRepository.save(authSession);
                    }
                    authSession.setTimeToLive(900 );
                    sessionRepository.save( authSession );
                }
            }
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/static/") 
            || path.startsWith("/login") 
            || path.startsWith("/error");
    }
}