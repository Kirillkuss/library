package com.itrail.library.service;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import com.itrail.library.service.auth.GoogleAuthenticationService;
import io.qameta.allure.Epic;
import io.qameta.allure.Owner;

@Owner( value = "Barysevich K. A." )
@Epic( value = "Тестирование сервиса - GoogleAuthenticationService" )
@DisplayName( "Тестирование сервиса - GoogleAuthenticationService" )
@ExtendWith(MockitoExtension.class)
public class GoogleAuthenticationServiceTest {

    @InjectMocks private GoogleAuthenticationService googleAuthenticationService;

    @Test
    @DisplayName("Генерация ключа")
    public void generateKeyTest(){
        assertNotEquals(googleAuthenticationService.generateKey(), googleAuthenticationService.generateKey());
    }

    @Test
    @DisplayName("Проверка ключа")
    public void isValidTest(){
        googleAuthenticationService.isValid("238472bjhasfyjksdfd", 212345 );
    }

    @Test
    @DisplayName("Генерация Qr-code ")
    public void generateQRImage(){
        googleAuthenticationService.generateQRImage( "23487923bjsdfhwer87sdgfsd", "admin1234");
    }
    
}
