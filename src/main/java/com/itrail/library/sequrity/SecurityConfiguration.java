package com.itrail.library.sequrity;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import com.itrail.library.sequrity.filter.LibSingleSessionFilter;
import com.itrail.library.sequrity.handler.LibAuthenticationFailureHandler;
import com.itrail.library.sequrity.handler.LibAuthenticationSuccessHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final LibAuthenticationFailureHandler libAuthenticationFailureHandler;
    private final LibAuthenticationSuccessHandler libAuthenticationSuccessHandler;
    private final LibSingleSessionFilter          libSingleSessionFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.addFilterBefore( libSingleSessionFilter, UsernamePasswordAuthenticationFilter.class )
                   .cors(cors -> cors.configurationSource( corsConfigurationSource() ))
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/login", "/logout", "/error", "/register", "/image-access-qr", "/clear-error-message", "/icon/**").permitAll()
                        .requestMatchers("/swagger-ui/index.html", "/**", "/").hasAnyRole("ADMIN", "USER")
                        .anyRequest().authenticated())
                .formLogin(login -> login
                        .loginPage("/login")
                        .loginProcessingUrl("/securecode") 
                        .defaultSuccessUrl("/securecode", true) 
                        .failureHandler(libAuthenticationFailureHandler)
                        .successHandler(libAuthenticationSuccessHandler) 
                        .permitAll())
                /**.sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))*/
                        .sessionManagement(session -> session
                .sessionFixation().changeSessionId()
                .maximumSessions(1))
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID"))
                .csrf(csrf -> csrf.disable())
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
                          configuration.setAllowedOrigins(List.of("*")); // Разрешить все домены
                          configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); 
                          configuration.setAllowedHeaders(List.of("*")); // Разрешить все заголовки
                          configuration.setAllowCredentials(false); // Отключаем передачу кук 
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                                        source.registerCorsConfiguration("/**", configuration); 
        return source;
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }
    
}
