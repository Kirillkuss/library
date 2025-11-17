package com.itrail.library.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.itrail.library.aspect.logger.ExecuteMethodLog;
import com.itrail.library.domain.Role;
import com.itrail.library.domain.User;
import com.itrail.library.repository.RoleRepository;
import com.itrail.library.repository.UserRepository;
import com.itrail.library.request.CreateUserRequest;
import com.itrail.library.response.BaseResponse;
import com.itrail.library.response.UserResponse;
import com.itrail.library.security.generate.PasswordGenerator;
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
            createUserRegister( new CreateUserRequest( "Admin123", 
                                            "AdmiN!?3957612#+=", 
                                           "ADMIN", 
                                          "ADMIN", 
                                          "ADMIN", 
                                               "ADMIN123@mail.ru", 
                                               "+375298934534", 
                                                     new HashSet<>(Arrays.asList("ADMIN"))), 2 );
            log.info( "Add user ADMIN");
        }
        if( userRepository.findByLogin( "User123" ).isEmpty() ){
            createUserRegister( new CreateUserRequest( "User123", 
                                            "AdmiN!?3957612#+=", 
                                           "USER", 
                                          "USER", 
                                          "USER", 
                                               "USER123@mail.ru", 
                                               "+375298933435", 
                                                     new HashSet<>(Arrays.asList("USER"))), 2);
            log.info( "Add user USER");
        }

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
    /**
     * Проверка размера и кол-во символов для пароля
     * @param password
     * @return boolean
     */
    private boolean isValidPassword(String password) {
        return password.matches("^(?=.*[0-9])(?=.*[a-zа-яё])(?=.*[A-ZА-ЯЁ])(?=.*[@#$%^&+=])(?=\\S+$).{12,}$");
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
     * Получение списка пользователей 
     * @param page - страница
     * @param size - размер
     * @return List UserResponse
     */
    //@Cacheable
    @ExecuteMethodLog 
    public List<UserResponse> getUsers( int page, int size ){
        List<UserResponse> users =
         userRepository.findAll( PageRequest.of( page - 1, size ))
                      .getContent()
                      .stream()
                      .map( user -> {
                        return new UserResponse( user.getLogin(), 
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
                      return users;
    }

    public List<UserResponse> findUsersForUI( String param, int page, int size ){
        return  userRepository.findUsersForUI( param, PageRequest.of( page - 1, size ) )
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

    }

    public boolean checkUserPassword( String rawPassword, String encodedPassword) {
        return passwordEncoder.matches( rawPassword, encodedPassword );
    }

    private record ValidationError(int code, String message) {}

    private BaseResponse<?> checkCreateUser( CreateUserRequest createUserRequest ) {
        return Stream.<Supplier<Optional<ValidationError>>>of( () -> validateLoginLength( createUserRequest.login() ),
                                                               () -> validatePasswordCheck( createUserRequest.password() ),
                                                               () -> validateEmail( createUserRequest.email() ),
                                                               () -> validateLoginExists( createUserRequest.login() ),
                                                               () -> validateEmailExists( createUserRequest.email() ),
                                                               () -> validatePhoneExists( createUserRequest.phone() ),
                                                               () -> validateRoles( createUserRequest.roles() ))
                    .map( Supplier::get )
                    .filter( Optional::isPresent )
                    .map( Optional::get )
                    .findFirst()
                    .map( error -> BaseResponse.error( error.code(), error.message() ))
                    .orElse( BaseResponse.success() ) ;
        
    }
    /**
     * Проверка на наличие роли
     * @param roles - список ролей
     * @return
     */
    /**private Optional<ValidationError> validateRoles(Set<String> roles) {
        return roles == null || roles.isEmpty() 
            ? Optional.of( new ValidationError(400, "Должна быть указана роль!"))
            : Optional.empty();
    }*/
    /**
     * Проверка на корректность ввода логина
     * @param login - логин
     * @return Optional ValidationError
     */
    private Optional<ValidationError> validateLoginLength( String login ){
        return login.length() <= 5 
            ? Optional.of( new ValidationError(400, "Длина логина должна быть не меньше 6 символов!"))
            : Optional.empty();
    }
    /**
     * Проверка на корректность ввода пароля
     * @param password - пароль
     * @return Optional ValidationError
     */
    private Optional<ValidationError> validatePasswordCheck( String password ){
        return !isValidPassword( password )
            ? Optional.of( new ValidationError( 400, "Неверный формат пароля, как минимум 12 знаков, 1 большая и одна 1 буква, 1 символ, 1 цифра!" ))
            : Optional.empty();
    }
    /**
     * Проверка на корректность ввода почты
     * @param email - почта
     * @return Optional ValidationError
     */
    private Optional<ValidationError> validateEmail( String email ){
        return !isValidEmail( email )
            ? Optional.of( new ValidationError( 400, "Неверный формат электронной почты!" ))
            : Optional.empty();
    }
    /**
     * Проверка на существ. пользоваетя по логину
     * @param login - логин
     * @return Optional ValidationError
     */
    private Optional<ValidationError> validateLoginExists( String login ){
        return userRepository.findByLogin( login ).isPresent()
            ? Optional.of( new ValidationError( 400 , "Пользователь с таким логином уже существует!" ))
            : Optional.empty();
    }
    /**
     * Проверка на существ. пользователя по почте
     * @param email - почта
     * @return Optional ValidationError
     */
    private Optional<ValidationError> validateEmailExists( String email ){
        return userRepository.findByEmail( email ).isPresent()
            ? Optional.of( new ValidationError( 400, "Пользователь с такой почтой уже существует!"))
            : Optional.empty();
    }
    /**
     * Проверка на наличие пользователя по телефону
     * @param phone - телефон
     * @return Optional ValidationError
     */
    private Optional<ValidationError> validatePhoneExists( String phone ){
        return userRepository.findUserByPhone( phone ).isPresent()
            ? Optional.of( new ValidationError( 400, "Пользователь с таким номером телефона уже зарегистрирован!"))
            : Optional.empty();
    }

    private Optional<ValidationError> validateRoles(Set<String> request) {
        if (request == null || request.isEmpty()) {
            return Optional.of(new ValidationError(400, "Укажите хотя бы одну роль!"));
        }
        for (String roleName : request) {
            Optional<Role> role = roleRepository.findByName(roleName);
            if (role.isEmpty()) {
                return Optional.of(new ValidationError(400, "Неверное наименование роли: " + roleName));
            }
        }
        return Optional.empty();
    }

    private Set<Role> getRoles( Set<String> request ){
        Iterator<String> iter = request.iterator();
        Set<Role> roles = new HashSet<>();
        while(iter.hasNext()){
            Optional<Role> role = roleRepository.findByName( iter.next());
            if( role.isPresent() ){
                roles.add( role.get() );
            } 
        }
        return roles;
    }

    /**
     * Добавление пользователя при регистрации
     * @param createUser - входной запрос при регистрации 
     * @return UserResponse
     */
    @Transactional
    public BaseResponse<UserResponse> createUserRegister( CreateUserRequest createUser, int type ){
        CreateUserRequest createUserRequest = getUserAuthRequest( createUser, type );
        BaseResponse response = checkCreateUser( createUserRequest );
        if( response.getStatus() != 200 ){
            return BaseResponse.error(response.getStatus(), response.getError() );
        }else{
            User user = userRepository.save( User.builder()
                                    .luDate( LocalDateTime.now() )
                                    .lastName( createUserRequest.lastName() ) 
                                    .firstName( createUserRequest.firstName() )
                                    .middleName( createUserRequest.middleName() )
                                    .login( createUserRequest.login() )
                                    .password( passwordEncoder.encode( createUserRequest.password() ))
                                    .email( createUserRequest.email() )
                                    .isOpen( false )
                                    .phone( createUserRequest.phone() )
                                    .secret( googleAuthenticationService.generateKey() )
                                    .roles( getRoles( createUserRequest.roles() ))
                                    .build() );
            return BaseResponse.success( new UserResponse( user.getLogin(), 
                                                           user.getLastName() + " " + user.getFirstName()+ " " + user.getMiddleName(), 
                                                           user.getEmail(), 
                                                           user.getPhone(), 
                                                           user.getIsOpen(), 
                                                           user.getRoles()
                                                                .stream()
                                                                .map( roles ->{return roles.getName();})
                                                                .collect( Collectors.toSet() ),
                                                            null ));
        }

    }

    private CreateUserRequest getUserAuthRequest( CreateUserRequest createUserRequest, int type ){
        Set<String> roles = new HashSet<>();
        if( type == 1 ){
            roles.add( "ADMIN");
        }else{
            roles = createUserRequest.roles();
        }
        return new CreateUserRequest( createUserRequest.login(),
                                      createUserRequest.password(),
                                      createUserRequest.lastName(),
                                      createUserRequest.firstName(),
                                      createUserRequest.middleName(),
                                      createUserRequest.email(),
                                      createUserRequest.phone(),
                                      roles );
    }

}
