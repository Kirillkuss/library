package com.itrail.library.service.auth;

import java.util.NoSuchElementException;
import java.util.Optional;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;
import com.itrail.library.config.redis.domain.Session;
import com.itrail.library.config.redis.repository.SessionRepository;
import com.itrail.library.domain.User;
import com.itrail.library.repository.UserRepository;
import com.itrail.library.service.UserService;
import lombok.RequiredArgsConstructor;
import java.awt.image.BufferedImage;
/**
 * Сервис для аутентификации пользователя в системе
 */
@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserService                 userService;
    private final UserRepository              userRepository;
    private final GoogleAuthenticationService googleAuthenticationService;
    private final SessionRepository           sessionRepository;
    private final SessionRegistry             sessionRegistry; 

    /**
     * двухфакторная аутентификация по логину и паролю - 1 часть
     * @param login     - логин
     * @param password  - пароль
     * @return String
     */
    public String authUser( String login, String password ){
        Optional<User> user = userRepository.findByLogin( login );
        if( sessionRepository.findByUsernameAndType( login, Session.SessionType.BLOCK ).isPresent() ) throw new BadCredentialsException( "Превышен лимит попыток!");
        if( user.isEmpty() )                                                                          throw new BadCredentialsException( "Неверный логин или пароль!");
        if( !userService.checkUserPassword( password, user.get().getPassword() ))                     throw new BadCredentialsException( "Неверный логин или пароль!");
        return user.orElseThrow().getLogin();
    }
    /**
     * двухфакторная аутентификация по коду  - 2 часть
     * @param username - логин
     * @param code - код 
     * @return User
     */
    public User verifyTwoFactorAuth( String username, Integer code) {
        User user = userRepository.findByLogin( username ).orElseThrow(() -> new NoSuchElementException("Not found user!"));
        if( ! googleAuthenticationService.isValid( user.getSecret(), code ) ) throw new BadCredentialsException("Invalid code!");
        return user;
    }
    /**
     * Генерация QR - кода 
     * @param username - логин пользователя
     * @return BufferedImage
     */
    public BufferedImage generateQR( String username) {
        User user = userRepository.findByLogin( username ).orElseThrow();
        return googleAuthenticationService.generateQRImage(user.getSecret(), username );
    }

    /**
     * Выход из системы для устаревшей сессии 
     * 
     * @param sessionId
     */
    public void logout(String sessionId) {
        clearSecurityContext(sessionId);
    }

    /**
     * Делаем истекшую сесессию в Redis недействительной
     * @param sessionId - Ид сессии
     */
    private void clearSecurityContext(String sessionId) {
        sessionRegistry.getAllPrincipals().forEach(principal -> {
            sessionRegistry.getAllSessions(principal, false)
                .stream()
                .filter(s -> s.getSessionId().equals(sessionId))
                .forEach(s -> {
                    s.expireNow(); 
                    SecurityContextHolder.clearContext(); 
                });
        });
    }
}
