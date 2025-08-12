package com.itrail.library.sequrity;

import java.util.List;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

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

    @Bean
    public SecurityFilterChain securityFilterChain( HttpSecurity http ) throws Exception {
         return http.cors(cors -> cors.configurationSource( corsConfigurationSource() ))
                    .authorizeHttpRequests(requests -> requests.requestMatchers( publicEndpoints()).permitAll()
                                                               .requestMatchers( privateEndpoint()).hasAnyRole("ADMIN", "USER")
                                                               .anyRequest().authenticated())
                    .formLogin(login -> login
                            .loginPage("/login")
                            .loginProcessingUrl("/login") 
                            .defaultSuccessUrl("/library", true) 
                            .failureHandler( libAuthenticationFailureHandler )
                            .successHandler( libAuthenticationSuccessHandler ) 
                            .permitAll())
                            .sessionManagement( session -> session.sessionFixation()
                                                                  .changeSessionId()
                                                                  .maximumSessions( 1 ))
                    .logout( logout -> logout
                        .logoutUrl( "/logout" )
                        .logoutSuccessUrl( "/library/login?logout=true" )
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")) 
                    .csrf(csrf -> csrf.disable()).build();
    }

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilterRegistration() {
        FilterRegistrationBean<CorsFilter> registration = new FilterRegistrationBean<>();
                                           registration.setFilter( new CorsFilter( corsConfigurationSource() ));
                                           registration.setOrder( Ordered.HIGHEST_PRECEDENCE );
        return registration;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:4200")); // Только Angular origin
        configuration.setAllowedMethods(List.of("GET", "POST", "DELETE", "OPTIONS")); // Добавить OPTIONS
        configuration.setAllowedHeaders(List.of(
            "Authorization",
            "Content-Type",
            "X-Requested-With",
            "Accept",
            "Origin",
            "X-XSRF-TOKEN" // Для CSRF
        ));
        configuration.setExposedHeaders(List.of(
            "Content-Disposition",
            "Content-Length",
            "X-Custom-Header",
            "X-XSRF-TOKEN" // Для CSRF
        ));
        configuration.setMaxAge(1800L);
        configuration.setAllowCredentials(true); // Важно для передачи кук
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Применять ко всем путям
        return source;
    }


    /**@Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize -> authorize.anyRequest()
            .permitAll())
            .csrf(csrf -> csrf.disable()); 
        return http.build();
    }*/

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
            "/index",
            "/library",
            "/library/**",
            "/library/swagger-ui/index.html",
            "/library/app/index.html"
        };
    }

    private String[] csrfIgnoringRequestMatchers(){
        return new String[]{"/login",
                            "/securecode",
                            "/logout",
                            "/error",
                            "/register",
                            "/change-password"};
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }


    
}
