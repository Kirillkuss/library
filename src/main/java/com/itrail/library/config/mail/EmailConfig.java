package com.itrail.library.config.mail;

import java.util.Properties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
@PropertySource(value = { "classpath:email.properties" })
public class EmailConfig {

    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.port}")
    private int port;

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;

    @Value("${spring.mail.properties.mail.smtp.auth}")
    private String auth;

    @Value("${spring.mail.properties.mail.smtp.starttls.enable}")
    private String enable;

    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl javaMailSenderImpl = new JavaMailSenderImpl();
                           javaMailSenderImpl.setHost(host );
                           javaMailSenderImpl.setPort( port );
                           javaMailSenderImpl.setUsername( username );
                           javaMailSenderImpl.setPassword( password );
        Properties properties = javaMailSenderImpl.getJavaMailProperties();
                   properties.put("mail.transport.protocol", "smtp");
                   properties.put("mail.smtp.auth", auth);
                   properties.put("mail.smtp.starttls.enable", enable);
        return javaMailSenderImpl;
    }
    
}
