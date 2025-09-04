package com.itrail.library.service;

import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import com.itrail.library.domain.User;
import com.itrail.library.repository.UserRepository;
import com.itrail.library.service.mail.EmailService;
import io.qameta.allure.Epic;
import io.qameta.allure.Owner;

@Owner(value = "Barysevich K. A.")
@Epic(value = "Тестирование сервиса - MailService")
@DisplayName("Тестирование сервиса - MailService")
@ExtendWith(MockitoExtension.class)
public class MailServiceTest {

    @Mock private JavaMailSender javaMailSender;
    @Mock private UserRepository userRepository;
    @Mock private UserService userService;

    @InjectMocks EmailService emailService;

    @Test
    @DisplayName("Отправка на почту нового пароля пользователю")
    public void sendNewPasswordToEmailTest(){
        String userinfo = "login";
        User user = new User( 1L, LocalDateTime.now(), "login", "VdmiN4567!?34545#+=", "login", "login", "login", "login@mail.com", false, "phone", "secret", null );
        Mockito.when( userRepository.findByChangePassword( userinfo )).thenReturn( Optional.of( user ));
        Mockito.when( userService.generateNewPasswordForUser( user )).thenReturn( "VdmiN4567!?34545#+=");
        emailService.sendNewPasswordToEmail( userinfo );
    }

    @Test
    @DisplayName("Отправка на почту нового пароля пользователю")
    public void sendNewPasswordToEmailTwoTest(){
        String userinfo = "login";
        User user = new User( 1L, LocalDateTime.now(), "login", "VdmiN4567!?34545#+=", "login", "login", "login", "lon@mail.com", false, "phone", "secret", null );
        Mockito.when( userRepository.findByChangePassword( userinfo )).thenReturn( Optional.of( user ));
        Mockito.when( userService.generateNewPasswordForUser( user )).thenReturn( "VdmiN4567!?34545#+=");
        emailService.sendNewPasswordToEmail( userinfo );
    }


    @Test
    @DisplayName("Отправка на почту нового пароля пользователю - Ошибка: пользователь не найден")
    public void sendNewPasswordToEmailErrorUserTest(){
        String userinfo = "login";
        Mockito.when( userRepository.findByChangePassword( userinfo )).thenReturn( Optional.empty());
        IllegalArgumentException exception = Assertions.assertThrows( IllegalArgumentException.class, () -> emailService.sendNewPasswordToEmail( userinfo ));
        Assertions.assertEquals("Пользователь не найден!", exception.getMessage());
    }

    @Test
    @DisplayName("Отправка на почту нового пароля пользователю - Ошибка: неверный формат почты")
    public void sendNewPasswordToEmailErrorEmailTest(){
        String userinfo = "login";
        User user = new User( 1L, LocalDateTime.now(), "login", "VdmiN4567!?34545#+=", "login", "login", "login", null, false, "phone", "secret", null );
        Mockito.when( userRepository.findByChangePassword( userinfo )).thenReturn( Optional.of( user ));
        Mockito.when( userService.generateNewPasswordForUser( user )).thenReturn( "VdmiN4567!?34545#+=");
        IllegalArgumentException exception = Assertions.assertThrows( IllegalArgumentException.class, () -> emailService.sendNewPasswordToEmail( userinfo ));
        Assertions.assertEquals("Неверный формат почты!", exception.getMessage());
    }

    @Test
    @DisplayName("Отправка на почту нового пароля пользователю - Ошибка: неверный формат почты")
    public void sendNewPasswordToEmailErrorEmailTwoTest(){
        String userinfo = "login";
        User user = new User( 1L, LocalDateTime.now(), "login", "VdmiN4567!?34545#+=", "login", "login", "login", "@mail.con", false, "phone", "secret", null );
        Mockito.when( userRepository.findByChangePassword( userinfo )).thenReturn( Optional.of( user ));
        Mockito.when( userService.generateNewPasswordForUser( user )).thenReturn( "VdmiN4567!?34545#+=");
        IllegalArgumentException exception = Assertions.assertThrows( IllegalArgumentException.class, () -> emailService.sendNewPasswordToEmail( userinfo ));
        Assertions.assertEquals("Неверный формат почты!", exception.getMessage());
    }


    
}
