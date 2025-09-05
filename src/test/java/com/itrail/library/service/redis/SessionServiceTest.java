package com.itrail.library.service.redis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import com.itrail.library.config.redis.domain.Session;
import com.itrail.library.config.redis.domain.Session.SessionType;
import com.itrail.library.config.redis.repository.SessionRepository;
import com.itrail.library.config.redis.service.SessionService;
import io.qameta.allure.Epic;
import io.qameta.allure.Owner;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Owner(value = "Barysevich K. A.")
@Epic(value = "Тестирование сервиса - SessionService")
@DisplayName("Тестирование сервиса - SessionService")
@ExtendWith(MockitoExtension.class)
public class SessionServiceTest {

    @Mock private SessionRepository sessionRepository;
    @Mock private HttpServletRequest httpServletRequest;
    @Mock private HttpSession httpSession;

    @InjectMocks private SessionService sessionService;

    @Test
    @DisplayName( "Успешное удаление сессии")
    public void deleteCurrentSessionTest( ) throws IllegalAccessException{
        String sessionId = "23498395023-34578234-345345";
        String username = "test";
        Mockito.when( httpServletRequest.getSession( false )).thenReturn(httpSession);
        Mockito.when( httpSession.getId()).thenReturn( sessionId );
        Mockito.when( httpSession.getAttribute("AUTH_USERNAME")).thenReturn(username);
        Session session = new Session();
        Mockito.doNothing().when( sessionRepository ).deleteBySessionId( sessionId );
        Mockito.when( sessionRepository.findByUsernameAndType( username, SessionType.AUTHENTICATED )).thenReturn( Optional.of( session ));
        sessionService.deleteCurrentSession( httpServletRequest );
    }

    @Test
    @DisplayName( "Неуспешное удаление сессии")
    public void deleteCurrentSessionErrorTest( ) throws IllegalAccessException{
        String currentSessionId = "23498395023-34578234-345345";
        String username = "test-user";
        Mockito.when( httpServletRequest.getSession(false)).thenReturn(httpSession);
        Mockito.when( httpSession.getId()).thenReturn(currentSessionId);
        Mockito.when( httpSession.getAttribute("AUTH_USERNAME")).thenReturn(username);
        Mockito.when(sessionRepository.findByUsernameAndType(username, SessionType.AUTHENTICATED)) .thenReturn(Optional.empty());
        sessionService.deleteCurrentSession(httpServletRequest);
        Mockito.verify( sessionRepository, Mockito.never()).deleteBySessionId(anyString());
        Mockito.verify( httpSession, Mockito.never()).invalidate();
    }

    @Test
    @DisplayName( "Неуспешное удаление сессии")
    public void deleteCurrentSessionErrorTestTwo( ) throws IllegalAccessException{
        String currentSessionId = "23498395023-34578234-345345";
        String username = null;
        Mockito.when(httpServletRequest.getSession(false)).thenReturn(httpSession);
        Mockito.when(httpSession.getId()).thenReturn(currentSessionId);
        Mockito.when(httpSession.getAttribute("AUTH_USERNAME")).thenReturn(username);
        sessionService.deleteCurrentSession(httpServletRequest);
        Mockito.verify(sessionRepository, Mockito.never()).deleteBySessionId(anyString());
        Mockito.verify(httpSession, Mockito.never()).invalidate();
    }

    @Test
    @DisplayName( "Неуспешное удаление сессии - Ошибка:нет прав")
    void deleteCurrentSessionTestError() throws IllegalAccessException {
        IllegalAccessException exception = Assertions.assertThrows( IllegalAccessException.class, () -> sessionService.deleteCurrentSession(httpServletRequest));
        Assertions.assertEquals("Нет прав!", exception.getMessage());
    }


    @Test
    @DisplayName( "Получение списка сессий")
    public void getSessionsTest(){
        String currentSessionId = "327234672344094374";
        String username         = "test";
        List<Session> sessionsWithNulls = Arrays.asList(new Session( currentSessionId, SessionType.AUTHENTICATED , username, 1 ), null);
        Mockito.when(sessionRepository.findAll()).thenReturn( sessionsWithNulls );
        Iterator<Session> result = sessionService.getSessions();
        assertNotNull(result);
        assertTrue(result.hasNext());
        assertEquals(sessionsWithNulls.get(0), result.next());
        assertFalse(result.hasNext());
        verify(sessionRepository, times(1)).findAll();
    }

}
