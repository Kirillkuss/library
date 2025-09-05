package com.itrail.library.service.auth;

import java.awt.image.BufferedImage;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import com.itrail.library.config.redis.domain.Session;
import com.itrail.library.config.redis.repository.SessionRepository;
import com.itrail.library.domain.User;
import com.itrail.library.repository.UserRepository;
import com.itrail.library.service.UserService;
import io.qameta.allure.Epic;
import io.qameta.allure.Owner;

@Owner(value = "Barysevich K. A.")
@Epic(value = "Тестирование сервиса - AuthService")
@DisplayName("Тестирование сервиса - AuthService")
@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock private UserService                 userService;
    @Mock private UserRepository              userRepository;
    @Mock private GoogleAuthenticationService googleAuthenticationService;
    @Mock private SessionRepository           sessionRepository;
    @Mock private SessionRegistry             sessionRegistry;

    @InjectMocks private AuthService authService;

    @Test
    @DisplayName("Авторизация пользователя по логину и паролю")
    public void authUserTest(){
        String login = "Admin321";
        String password = "N@gdk3!?kdh34#+=";
        User user = new User( 1L, LocalDateTime.now(), "Admin321", "N@gdk3!?kdh34#+=", "Admin321", "Admin321", "Admin321", "Admin321@mail.com", false, "phone", "secret", null );
        Mockito.when( userRepository.findByLogin( login)).thenReturn(Optional.of( user ));
        Mockito.when( sessionRepository.findByUsernameAndType( login, Session.SessionType.BLOCK ) ).thenReturn( Optional.empty() );
        Mockito.when( userService.checkUserPassword( password, password )).thenReturn( true );
        authService.authUser( login, password );
    }

    @Test
    @DisplayName("Авторизация пользователя по логину и паролю - Ошибка неверный логин")
    public void authUserErrorUserTest(){
        String login = "Admin321";
        String password = "N@gdk3!?kdh34#+=";
        User user = new User( 1L, LocalDateTime.now(), "Admin321", "N@gdk3!?kdh34#+=", "Admin321", "Admin321", "Admin321", "Admin321@mail.com", false, "phone", "secret", null );
        Mockito.when( userRepository.findByLogin( login)).thenReturn(Optional.empty());
        BadCredentialsException exception = Assertions.assertThrows( BadCredentialsException.class, () -> authService.authUser( login, password ));
        Assertions.assertEquals("Неверный логин или пароль!", exception.getMessage());
    }

    @Test
    @DisplayName("Авторизация пользователя по логину и паролю - Ошибка пользователь заблокирован")
    public void authUserErrorSessionTest(){
        String login = "Admin321";
        String password = "N@gdk3!?kdh34#+=";
        User user = new User( 1L, LocalDateTime.now(), "Admin321", "N@gdk3!?kdh34#+=", "Admin321", "Admin321", "Admin321", "Admin321@mail.com", false, "phone", "secret", null );
        Mockito.when( userRepository.findByLogin( login)).thenReturn(Optional.of( user ));
        Mockito.when( sessionRepository.findByUsernameAndType( login, Session.SessionType.BLOCK ) ).thenReturn( Optional.of( new Session()) );
        BadCredentialsException exception = Assertions.assertThrows( BadCredentialsException.class, () -> authService.authUser( login, password ));
        Assertions.assertEquals("Превышен лимит попыток!", exception.getMessage());
    }

    @Test
     @DisplayName("Авторизация пользователя по логину и паролю - Ошибка неверный пароль")
    public void authUserErrorPasswordTest(){
        String login = "Admin321";
        String password = "N@gdk3!?kdh34#+=";
        User user = new User( 1L, LocalDateTime.now(), "Admin321", "N@gdk3!?kdh34#+=", "Admin321", "Admin321", "Admin321", "Admin321@mail.com", false, "phone", "secret", null );
        Mockito.when( userRepository.findByLogin( login)).thenReturn(Optional.of( user ));
        Mockito.when( sessionRepository.findByUsernameAndType( login, Session.SessionType.BLOCK ) ).thenReturn( Optional.empty());
        Mockito.when( userService.checkUserPassword( password, password )).thenReturn( false );
        BadCredentialsException exception = Assertions.assertThrows( BadCredentialsException.class, () -> authService.authUser( login, password ));
        Assertions.assertEquals("Неверный логин или пароль!", exception.getMessage());
    }

    @Test
    @DisplayName("Авторизация пользователя по логину и коду")
    public void verifyTwoFactorAuthTest(){
        User user = new User( 1L, LocalDateTime.now(), "Admin321", "N@gdk3!?kdh34#+=", "Admin321", "Admin321", "Admin321", "Admin321@mail.com", false, "phone", "secret", null );
        Mockito.when( userRepository.findByLogin( user.getLogin() )).thenReturn(Optional.of( user ));
        Mockito.when( googleAuthenticationService.isValid( user.getSecret(), 122444 )).thenReturn( true );
        authService.verifyTwoFactorAuth(user.getLogin(), 122444 );
    }

    @Test
    @DisplayName("Авторизация пользователя по логину и коду - Ошибка неверный пользователь")
    public void verifyTwoFactorAuthErrorUserTest(){
        User user = new User( 1L, LocalDateTime.now(), "Admin321", "N@gdk3!?kdh34#+=", "Admin321", "Admin321", "Admin321", "Admin321@mail.com", false, "phone", "secret", null );
        Mockito.when( userRepository.findByLogin( user.getLogin() )).thenReturn(Optional.empty());
        NoSuchElementException exception = Assertions.assertThrows( NoSuchElementException.class, () -> authService.verifyTwoFactorAuth(user.getLogin(), 122444 ));
        Assertions.assertEquals("Not found user!", exception.getMessage());
    }

    @Test
    @DisplayName("Авторизация пользователя по логину и коду - Ошибка неверный код")
    public void verifyTwoFactorAuthErrorCodeTest(){
        User user = new User( 1L, LocalDateTime.now(), "Admin321", "N@gdk3!?kdh34#+=", "Admin321", "Admin321", "Admin321", "Admin321@mail.com", false, "phone", "secret", null );
        Mockito.when( userRepository.findByLogin( user.getLogin() )).thenReturn(Optional.of( user ));
        Mockito.when( googleAuthenticationService.isValid( user.getSecret(), 122444 )).thenReturn( false );
        BadCredentialsException exception = Assertions.assertThrows( BadCredentialsException.class, () -> authService.verifyTwoFactorAuth(user.getLogin(), 122444 ));
        Assertions.assertEquals("Invalid code!", exception.getMessage());
    }

    @Test
    public void generateQRCode(){
        String username = "first325";
        User user = new User( 1L, LocalDateTime.now(), "first325", "N@gdk3!?kdh34#+=", "first325", "first325", "first325", "Admin321@mail.com", false, "phone", "secret", null );
        Mockito.when( userRepository.findByLogin( username )).thenReturn( Optional.of( user ));
        BufferedImage bufferedImage = new BufferedImage(100, 100, 1);
        Mockito.when( googleAuthenticationService.generateQRImage(user.getSecret(), username )).thenReturn( bufferedImage );
        authService.generateQR( username );
    }

    @Test
    public void logoutTest(){
        String sessionId = "23424-454566565-67442-2334";
        List<Object> obj = List.of( new Object() );
        List<SessionInformation> sessions = List.of( new SessionInformation(obj.get(0),sessionId, Date.from(Instant.now()) ));
        Mockito.when( sessionRegistry.getAllPrincipals()).thenReturn( obj );
        Mockito.when( sessionRegistry.getAllSessions(  obj.getFirst() , false)).thenReturn( sessions );
        authService.logout( sessionId );
    }
    
}
