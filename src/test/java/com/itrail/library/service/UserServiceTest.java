package com.itrail.library.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.itrail.library.domain.Role;
import com.itrail.library.domain.User;
import com.itrail.library.repository.RoleRepository;
import com.itrail.library.repository.UserRepository;
import com.itrail.library.request.CreateUserRequest;
import com.itrail.library.response.BaseResponse;
import com.itrail.library.response.UserResponse;
import com.itrail.library.security.generate.PasswordGenerator;
import com.itrail.library.service.auth.GoogleAuthenticationService;

import io.qameta.allure.Allure;
import io.qameta.allure.Epic;
import io.qameta.allure.Owner;

@Owner(value = "Barysevich K. A.")
@Epic(value = "Тестирование сервиса - UserService")
@DisplayName("Тестирование сервиса - UserService")
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock private UserRepository              userRepository;
    @Mock private RoleRepository              roleRepository;
    @Mock private PasswordEncoder             passwordEncoder;
    @Mock private PasswordGenerator           passwordGenerator;
    @Mock private GoogleAuthenticationService googleAuthenticationService;

    @InjectMocks UserService userService;

    public final String TYPE     = "application/json";
    public final String rezult   = "Результат: ";

    @Test
    @DisplayName("Генерация нового пароля для пользователя")
    public void generateNewPasswordForUserTest(){
        User user = new User( 1L, LocalDateTime.now(), "login", "VdmiN4567!?34545#+=", "last", "first", "middle", "email", false, "phone", "secret", null );
        Mockito.when( passwordGenerator.generateRandomPassword() ).thenReturn( "VdmiN4567!?34545#+=" );
        Mockito.when( passwordEncoder.encode( user.getPassword() )).thenReturn( "VdmiN4567!?34545#+=" );
        Mockito.when( userRepository.save( user )).thenReturn(user);
        String result = userService.generateNewPasswordForUser( user );
        Allure.addAttachment( rezult, TYPE, result.toString() );
    }

    @Test
    @DisplayName("Генерация нового пароля для пользователя - Ошибка корректности пароля")
    public void generateNewPasswordForUserErrorTest(){
        User user = new User( 1L, LocalDateTime.now(), "login", "", "last", "first", "middle", "email", false, "phone", "secret", null );
        Mockito.when( passwordGenerator.generateRandomPassword() ).thenReturn( "" );
        IllegalArgumentException exception = Assertions.assertThrows( IllegalArgumentException.class, () -> userService.generateNewPasswordForUser( user ));
        Assertions.assertEquals("Неверный формат пароля! Пароль должен сожедржать не менее 12 символов, 1 букву верхнего и нижнего реестра, 1 цифру и 1 спец. символ ( *[@#$^&+=!№:?:%*(;_)}{]", exception.getMessage());
    }

    @ParameterizedTest
    @CsvSource({"1,10"})
    @DisplayName( "Ленивая загрузка пользователей")
    public void getUsersTest( int page, int size ){
        Role role = new Role(1L, LocalDateTime.now(), "ADMIN" );
        List<User> users = List.of( new User( 1L, LocalDateTime.now(), "Admin321", "VdmiN4567!?34545#+=", "last", "first", "middle", "mail1435@mail.ru", false, "+375284346506", "secret", Set.of( role )),
                                    new User( 2L, LocalDateTime.now(), "Admin3213", "VdmiN4567!?34545#+=", "last", "first", "middle", "mail1435@mail.ru", false, "+375284346506", "secret", Set.of( role )));
        Page<User> userPage = new PageImpl<>( users );
        Mockito.when(userRepository.findAll( PageRequest.of( page - 1, size ) )).thenReturn( userPage );
        List<UserResponse> result = userService.getUsers( page, size );
        Allure.addAttachment( rezult, TYPE, result.toString() );
    }

    @ParameterizedTest
    @CsvSource({"0,5"})
    @DisplayName("Проверка на корректность ввода страницы в методе getUsers")
    public void getUsersErrorPageTest( int page, int size) {
        IllegalArgumentException exception = Assertions.assertThrows( IllegalArgumentException.class, () -> userService.getUsers( page, size ));
        Assertions.assertEquals("Значение страницы должно быть больше нуля!", exception.getMessage());
    }

    @ParameterizedTest
    @CsvSource({"1,0"})
    @DisplayName("Проверка на корректность ввода размера страницы в методе getUsers ")
    public void getUsersErrorSizeTest( int page, int size) {
        IllegalArgumentException exception = Assertions.assertThrows( IllegalArgumentException.class, () -> userService.getUsers( page, size ));
        Assertions.assertEquals("Значение размера страницы должно быть больше нуля!", exception.getMessage());
    }


    @ParameterizedTest
    @CsvSource({"Admi, 1,10"})
    @DisplayName( "Ленивая загрузка пользователей с поиск по ФИО или логин, почта, телефон")
    public void findUsersForUITest( String param, int page, int size ){
        Role role = new Role(1L, LocalDateTime.now(), "ADMIN" );
        List<User> users = List.of( new User( 1L, LocalDateTime.now(), "Admin321", "VdmiN4567!?34545#+=", "last", "first", "middle", "mail1435@mail.ru", false, "+375284346506", "secret", Set.of( role )),
                                    new User( 2L, LocalDateTime.now(), "Admin3213", "VdmiN4567!?34545#+=", "last", "first", "middle", "mail1435@mail.ru", false, "+375284346506", "secret", Set.of( role )));
        Mockito.when(userRepository.findUsersForUI( param, PageRequest.of( page - 1, size ) )).thenReturn( users );
        List<UserResponse> result = userService.findUsersForUI( param, page, size );
        Allure.addAttachment( rezult, TYPE, result.toString() );
    }

    @ParameterizedTest
    @CsvSource({"Admi,0,5"})
    @DisplayName("Проверка на корректность ввода страницы в методе getUsers")
    public void findUsersForUIErrorPageTest( String param, int page, int size) {
        IllegalArgumentException exception = Assertions.assertThrows( IllegalArgumentException.class, () -> userService.findUsersForUI( param, page, size ));
        Assertions.assertEquals("Значение страницы должно быть больше нуля!", exception.getMessage());
    }

    @ParameterizedTest
    @CsvSource({"Admi,1,0"})
    @DisplayName("Проверка на корректность ввода размера страницы в методе getUsers ")
    public void findUsersForUIErrorSizeTest( String param, int page, int size) {
        IllegalArgumentException exception = Assertions.assertThrows( IllegalArgumentException.class, () -> userService.findUsersForUI( param, page, size ));
        Assertions.assertEquals("Значение размера страницы должно быть больше нуля!", exception.getMessage());
    }

    @Test
    @DisplayName( "Регистрация нового пользователя")
    public void createUserRegisterTest(){
        Set<String> roles =  Set.of( "ADMIN" );
        CreateUserRequest createUserRequest = new CreateUserRequest( "Admin321",
                                                                  "VdmiN4567!?34545#+=", 
                                                                  "last", 
                                                                 "first", 
                                                                "middle", 
                                                                     "mail1435@mail.ru", 
                                                                     "+375284346506", 
                                                                           roles );
        Role role = new Role(1L, LocalDateTime.now(), "ADMIN" );
        User user = new User( 1L, LocalDateTime.now(), "Admin321", "VdmiN4567!?34545#+=", "last", "first", "middle", "mail1435@mail.ru", false, "+375284346506", "secret", Set.of( role ));
        Allure.parameter( "createUserRequest", createUserRequest );
        Mockito.when( passwordEncoder.encode( createUserRequest.password() )).thenReturn( "VdmiN4567!?34545#+=" );
        Mockito.when( googleAuthenticationService.generateKey()).thenReturn( "VdmiN4567!?34545#+=" );
        Mockito.when( userRepository.findByLogin( createUserRequest.login() )).thenReturn( Optional.empty());
        Mockito.when( userRepository.findByEmail( createUserRequest.email() )).thenReturn( Optional.empty());
        Mockito.when( userRepository.findUserByPhone( createUserRequest.phone() )).thenReturn( Optional.empty());
        Mockito.when( roleRepository.findByName( roles.iterator().next() )).thenReturn( Optional.of( role ));
        Mockito.when( userRepository.save( Mockito.any( User.class ))).thenReturn( user );  
        BaseResponse<UserResponse> result = userService.createUserRegister( createUserRequest, 1 );
        Allure.addAttachment( rezult, TYPE, result.toString() );         
    }

    @Test
    @DisplayName( "Тестирование метода на проверку пароля")
    public void checkUserPasswordTest(){
        String password = "12345";
        String encodedPassword = "3afdsknjfi3245";
        Mockito.when( passwordEncoder.matches( password, encodedPassword )).thenReturn( Boolean.valueOf( false ));
        userService.checkUserPassword( password, encodedPassword );
    }

    @Test
    @DisplayName( "Инициализация двух пользователей")
    public void initTest(){
        Role ADMIN = new Role(1L, LocalDateTime.now(), "ADMIN" );
        Role USER = new Role(2L, LocalDateTime.now(), "USER" );
        Mockito.when( userRepository.findByLogin( "Admin123" )).thenReturn( Optional.empty() );
        Mockito.when( userRepository.findByLogin( "User123" )).thenReturn( Optional.empty() );
        Mockito.when( roleRepository.findByName( "ADMIN" )).thenReturn(Optional.of( ADMIN ));
        Mockito.when( roleRepository.findByName( "USER" )).thenReturn(Optional.of( USER ));  
        userService.init();
    }

    @Test
    @DisplayName( "Инициализация двух пользователей - проверка, если уже созданы") 
    public void initSecondTest(){
        Mockito.when( userRepository.findByLogin( "Admin123" )).thenReturn( Optional.of(new User()) );
        Mockito.when( userRepository.findByLogin( "User123" )).thenReturn( Optional.of(new User()) );
        userService.init();
    }

    
}
