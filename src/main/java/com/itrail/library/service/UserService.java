package com.itrail.library.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itrail.library.aspect.logger.ExecuteMethodLog;
import com.itrail.library.domain.Role;
import com.itrail.library.domain.User;
import com.itrail.library.repository.RoleRepository;
import com.itrail.library.repository.UserRepository;
import com.itrail.library.request.CreateUserRequest;
import com.itrail.library.response.UserResponse;
import com.itrail.library.sequrity.generate.PasswordGenerator;
import com.itrail.library.service.auth.GoogleAuthenticationService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
/**
 * Сервис для работы с пользователями
 */
@Slf4j
@CacheConfig(cacheNames={"users"})
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordGenerator passwordGenerator;
    private final GoogleAuthenticationService googleAuthenticationService;

    @PostConstruct
    public void init(){
        if( userRepository.findByLogin( "Admin123" ).isEmpty() ){
            createUser( new CreateUserRequest( "Admin123", 
                                            "Admin123!", 
                                           "ADMIN", 
                                          "ADMIN", 
                                          "ADMIN", 
                                               "ADMIN123@mail.ru", 
                                               "+375298934534", 
                                                     new HashSet<>(Arrays.asList("ADMIN"))));
            log.info( "Add user ADMIN");
        }
        if( userRepository.findByLogin( "User123" ).isEmpty() ){
            createUser( new CreateUserRequest( "User123", 
                                            "User123!", 
                                           "USER", 
                                          "USER", 
                                          "USER", 
                                               "USER123@mail.ru", 
                                               "+375298933435", 
                                                     new HashSet<>(Arrays.asList("USER"))));
            log.info( "Add user USER");
        }

    }

    /**
     * Проверка пользователя на добавление
     * @param createUserRequest - входной запрос
     */
    private void checkCreateUser( CreateUserRequest createUserRequest ){
        if ( createUserRequest.roles() == null || createUserRequest.roles().isEmpty() ) throw new IllegalArgumentException( "Должна быть указана роль!");
        if ( createUserRequest.login().length() <= 5 )                                  throw new IllegalArgumentException( "Длина логина должна быть не меньше 6 символов!");
        if ( !isValidPassword( createUserRequest.password() ))                          throw new IllegalArgumentException( "Неверный формат пароля, как минимум 8 знаков, 1 большая и одна 1 буква, 1 символ!");
        if ( !isValidEmail( createUserRequest.email() ))                                throw new IllegalArgumentException("Неверный формат электронной почты!");
        if ( userRepository.findByLogin( createUserRequest.login() ).isPresent() )      throw new IllegalArgumentException( "Пользователь с таким логином уже существует!");
        if ( userRepository.findByEmail( createUserRequest.email() ).isPresent() )      throw new IllegalArgumentException( "Пользователь с такой почтой уже существует!");
        if ( userRepository.findUserByPhone( createUserRequest.phone() ).isPresent() )  throw new IllegalArgumentException("Пользователь с таким номером телефона уже существует!");
    }

    /**
     * Провекра на корректность ввода почты
     * @param email  - почта
     * @return  boolean
     */
    private boolean isValidEmail(String email) {
        String emailRegex = "^[\\w-\\.]+@[\\w-]+\\.[a-zA-Z]{2,4}$";
        return email != null && email.matches(emailRegex);
    }

    private Set<Role> checkRoles( Set<String> request ){
        Iterator<String> iter = request.iterator();
        Set<Role> roles = new HashSet<>();
        while(iter.hasNext()){
            Optional<Role> role = roleRepository.findByName( iter.next());
            if( role.isEmpty() ) throw new IllegalArgumentException( "Неверное наименование роли!");
            roles.add( role.orElseThrow() );
        }
        return roles;
    }

    /**
     * Проверка размера и кол-во символов для пароля
     * @param password
     * @return boolean
     */
    private boolean isValidPassword(String password) {
        return password.matches("^(?=.*[0-9])(?=.*[a-zа-яё])(?=.*[A-ZА-ЯЁ])(?=.*[@#$%^&+=])(?=\\S+$).{12,20}$");
    }


    @Transactional
    public String generateNewPasswordForUser( User user ){
        String password = passwordGenerator.generateRandomPassword();
        validatePassword( password );
        user.setPassword( passwordEncoder.encode( password ));
        userRepository.save( user );
        return password;
    }

    private void validatePassword(String password) {
        if (!isValidPassword(password)) {
            throw new IllegalArgumentException("Неверный формат пароля! Пароль должен сожедржать не менее 12 символов, 1 букву верхнего и нижнего реестра, 1 цифру и 1 спец. символ ( *[@#$^&+=!№:?:%*(;_)}{]" );
        }
    }

    /**
     * Добавление пользователя
     * @param createUserRequest
     */
    @Transactional
    public void createUser( CreateUserRequest createUserRequest ){
        checkCreateUser( createUserRequest );
        userRepository.save( User.builder()
                                 .luDate( LocalDateTime.now() )
                                 .lastName( createUserRequest.lastName() ) 
                                 .firstName( createUserRequest.firstName() )
                                 .middleName( createUserRequest.middleName() )
                                 .login( createUserRequest.login() )
                                 .password( passwordEncoder.encode( createUserRequest.password() ))
                                 .email( createUserRequest.email() )
                                 .isOpen( true )
                                 .phone( createUserRequest.phone() )
                                 .secret( googleAuthenticationService.generateKey() )
                                 .roles( checkRoles( createUserRequest.roles() ))
                                 .build() );
    }
    /**
     * Получение списка пользователей 
     * @param page - страница
     * @param size - размер
     * @return List UserResponse
     */
    @Cacheable
    @ExecuteMethodLog 
    public List<UserResponse> getUsers( int page, int size ){
        List<UserResponse> users =
         userRepository.findAll( PageRequest.of( page - 1, size ))
                      .getContent()
                      .stream()
                      .map( user -> {
                        return new UserResponse(user.getLogin(), 
                                                user.getLastName() + " " + user.getFirstName()+ " " + user.getMiddleName(), 
                                                user.getEmail(), 
                                                user.getPhone(), 
                                                user.getIsOpen(), 
                                                user.getRoles()
                                                    .stream()
                                                    .map( roles ->{
                                                        return roles.getName();
                                                    }).collect( Collectors.toSet() ), null);
                      }).toList();
                      if( users.isEmpty() ) throw new NoSuchElementException("По данному запросу ничего не найдено!");
                      return users;
    }

    public boolean checkUserPassword( String rawPassword, String encodedPassword) {
        return passwordEncoder.matches( rawPassword, encodedPassword );
    }
    /**
     * Добавление пользователя при регистрации
     * @param createUser - входной запрос при регистрации 
     * @return UserResponse
     */
    @Transactional
    public UserResponse createUserRegister( CreateUserRequest createUser ){
        CreateUserRequest createUserRequest = getUserAuthRequest( createUser );
        checkCreateUser( createUserRequest );
        User user = userRepository.save( User.builder()
                                 .luDate( LocalDateTime.now() )
                                 .lastName( createUserRequest.lastName() ) 
                                 .firstName( createUserRequest.firstName() )
                                 .middleName( createUserRequest.middleName() )
                                 .login( createUserRequest.login() )
                                 .password( passwordEncoder.encode( createUserRequest.password() ))
                                 .email( createUserRequest.email() )
                                 .isOpen( true )
                                 .phone( createUserRequest.phone() )
                                 .secret( googleAuthenticationService.generateKey() )
                                 .roles( checkRoles( createUserRequest.roles() ))
                                 .build() );
        return new UserResponse( user.getLogin(), 
                                 user.getLastName() + " " + user.getFirstName()+ " " + user.getMiddleName(), 
                                 user.getEmail(), 
                                 user.getPhone(), 
                                 user.getIsOpen(), 
                                 user.getRoles()
                                     .stream()
                                     .map( roles ->{return roles.getName();})
                                     .collect( Collectors.toSet() ),
                                 null );
    }

    private CreateUserRequest getUserAuthRequest( CreateUserRequest createUserRequest){
        return new CreateUserRequest( createUserRequest.login(),
                                      createUserRequest.password(),
                                      createUserRequest.lastName(),
                                      createUserRequest.firstName(),
                                      createUserRequest.middleName(),
                                      createUserRequest.email(),
                                      createUserRequest.phone(),
                                      Set.of("ADMIN") );
    }

}
