package com.itrail.library.sequrity.handler;

import java.io.IOException;
import java.util.Optional;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import com.itrail.library.config.redis.repository.SessionRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.itrail.library.config.redis.domain.Session;

@Slf4j
@Component
@RequiredArgsConstructor
public class LibAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private static final int MAX_ATTEMPTS = 3;
    private final SessionRepository sessionRepository;

    @Override
    public void onAuthenticationFailure( HttpServletRequest httpServletRequest,
                                         HttpServletResponse httpServletResponse,
                                         AuthenticationException exception) throws IOException {
        
        HttpSession session = httpServletRequest.getSession(false);
        if (session == null) {
            httpServletResponse.sendRedirect("/login");
            return;
        }
        
        String username = (String) session.getAttribute("AUTH_USERNAME");
        if (username == null) {
            httpServletResponse.sendRedirect("/login");
            return;
        }

        String httpSessionId = session.getId();
        if (isUserBlocked(username)) {
            log.warn("User {} is blocked", username);
            httpServletResponse.sendRedirect("/login?blocked=true");
            return;
        }

        // Увеличиваем счетчик попыток для пользователя
        int attempts = incrementUserAttempts(username, httpSessionId);
        log.debug("Attempts count for user {}: {}", username, attempts);
        if (attempts >= MAX_ATTEMPTS) {
            log.warn("Blocking user {} after {} failed attempts", username, attempts);
            blockUser(username, httpSessionId);
            sessionRepository.deleteByUsernameAndType(username, Session.SessionType.ATTEMPT);
            httpServletRequest.getSession().invalidate();
            httpServletResponse.sendRedirect("/login?error=blocked");
            return;
        }


        httpServletRequest.getSession().setAttribute("error", String.format("Неверный код! Осталось попыток: %d", MAX_ATTEMPTS - attempts));
        httpServletResponse.sendRedirect("/securecode");
        
    }

    public void convertToAuthenticated(String httpSessionId, String username) {
        if (username != null) {
            sessionRepository.deleteByUsernameAndType(username, Session.SessionType.ATTEMPT);
            sessionRepository.save( new Session( httpSessionId, Session.SessionType.AUTHENTICATED, username, 0 ));
        }
    }

    private boolean isUserBlocked(String username) {
        return sessionRepository.findByUsernameAndType(username, Session.SessionType.BLOCK).isPresent();
    }

    private int incrementUserAttempts(String username, String currentSessionId) {
        Optional<Session> existingSession = sessionRepository.findByUsernameAndType(username, Session.SessionType.ATTEMPT);
        if (existingSession.isPresent()) {
            Session session = existingSession.get();
            if (!session.getSessionId().equals(currentSessionId)) {
                session.setSessionId(currentSessionId);
            }
            session.setAttempts( session.getAttempts() + 1);
            sessionRepository.save( session );
            return session.getAttempts();
        } else {
            sessionRepository.save( new Session( currentSessionId, Session.SessionType.ATTEMPT, username, 1 ));
            return 1;
        }
    }

    private void blockUser(String username, String sessionId) {
        sessionRepository.findByUsernameAndType( username, Session.SessionType.BLOCK)
            .ifPresentOrElse(
                existing -> log.debug("User {} already blocked", username),
                () -> {
                    sessionRepository.save( new Session(sessionId, Session.SessionType.BLOCK, username,  MAX_ATTEMPTS ));
                    log.info("Created new block for user {}", username);
                }
            );
    }

}