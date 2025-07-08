package com.itrail.library.service.mail;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import com.itrail.library.domain.User;
import com.itrail.library.repository.UserRepository;
import com.itrail.library.service.UserService;
import lombok.RequiredArgsConstructor;

@Service
@PropertySource(value = { "classpath:email.properties" })
@RequiredArgsConstructor
public class EmailService {

    @Value("${spring.mail.username}")
    private String mail;

    private final JavaMailSender javaMailSender;
    private final UserRepository userRepository;
    private final UserService userService;
    /**
     * Отправка нового пароля для пользователя
     * @param userinfo - логин, почта или номер телефона
     * @return String
     */
    public String sendNewPasswordToEmail( String userinfo ){
        Optional<User> user = userRepository.findByChangePassword( userinfo );
        if( !user.isPresent() ) throw new IllegalArgumentException("Пользователь не найден!");
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
                          simpleMailMessage.setTo( user.get().getEmail());
                          simpleMailMessage.setSubject( "Изменение пароля");
                          simpleMailMessage.setText( "Ваш пароль был изменен, используйте этот: " + userService.generateNewPasswordForUser( user.get() ));
                          simpleMailMessage.setFrom( mail );
        javaMailSender.send( simpleMailMessage );
        return maskEmail(user.get().getEmail());
    }

    /**
     * Маска для электронной почты
     * @param email - электронная почта пользователя
     * @return String
     */
    private String maskEmail(String email) {
        if (email == null || email.isEmpty()) {
            return "[empty]";
        }
        int atIndex = email.indexOf('@');
        if (atIndex <= 0) {
            return "[invalid]";
        }
        String namePart = email.substring(0, atIndex);
        String domainPart = email.substring(atIndex);
        if (namePart.length() <= 3) {
            return namePart.charAt(0) + "***" + domainPart;
        }
        String maskedName = String.valueOf(namePart.charAt(0));
        for (int i = 1; i < namePart.length() - 2; i++) {
            maskedName += "*";
        }
        maskedName += namePart.substring(namePart.length() - 2);
        return maskedName + domainPart;
    }
    
}
