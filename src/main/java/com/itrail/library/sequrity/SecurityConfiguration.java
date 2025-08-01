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
                            .requestMatchers( publicEndpoints()).permitAll()
                            .requestMatchers( privateEndpoint()).hasAnyRole("ADMIN", "USER")
                            .anyRequest().authenticated())
                    .formLogin(login -> login
                            .loginPage("/login")
                            .loginProcessingUrl("/securecode") 
                            .defaultSuccessUrl("/securecode", true) 
                            .failureHandler(libAuthenticationFailureHandler)
                            .successHandler(libAuthenticationSuccessHandler) 
                            .permitAll())
                            .sessionManagement( session -> session
                                .sessionFixation()
                                .changeSessionId()
                                .maximumSessions(1))
                    .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID"))
                    .csrf(csrf -> csrf.disable())
                    .build();
    }

    /**@Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize -> authorize.anyRequest()
            .permitAll())
            .csrf(csrf -> csrf.disable()); 
        return http.build();
    }*/

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
                          configuration.setAllowedOrigins(List.of("*"));
                          configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); 
                          configuration.setAllowedHeaders(List.of("*")); 
                          configuration.setAllowCredentials(false);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                                        source.registerCorsConfiguration("/**", configuration); 
        return source;
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    private String[] publicEndpoints() {
        return new String[]{
            "/login",
            "/logout",
            "/change-password",
            "/error",
            "/register",
            "/clear-error-message",
            "/icon/**",
            "/css/**"
        };
    }

    private String[] privateEndpoint(){
        return new String[]{
            "/swagger-ui/index.html", 
            "/**",
            "/",
            "/index"
        };
    }
    
}
