package com.itrail.library.security;

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
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import com.itrail.library.security.filter.LibSingleSessionFilter;
import com.itrail.library.security.handler.LibAuthenticationFailureHandler;
import com.itrail.library.security.handler.LibAuthenticationSuccessHandler;
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
    public SecurityFilterChain securityFilterChain( HttpSecurity http ) throws Exception {
         return http.addFilterBefore( libSingleSessionFilter, UsernamePasswordAuthenticationFilter.class )
                    .cors(cors -> cors.configurationSource( corsConfigurationSource() ))
                    .authorizeHttpRequests(requests -> requests.requestMatchers( publicEndpoints()).permitAll()
                                                               .requestMatchers( privateEndpoint()).hasAnyRole("ADMIN", "USER")
                                                               .anyRequest().authenticated())
                    .formLogin(login -> login
                            .loginPage("/login")
                            .loginProcessingUrl("/securecode") 
                            .defaultSuccessUrl("/library/securecode", true) 
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
                        .deleteCookies("JSESSIONID", "XSRF-TOKEN", "X-XSRF-TOKEN")) 
                    .csrf(csrf -> csrf.csrfTokenRepository(csrfTokenRepository())
                                      .ignoringRequestMatchers(csrfIgnoringRequestMatchers()))
                    //.csrf(csrf -> csrf.disable())
                    .build();
    }

    private String[] csrfIgnoringRequestMatchers(){
        return new String[]{ "/login", "/securecode", "/logout", "/error","/register", 
            "/change-password","/users/**","/cards/**","/videos/**" ,"/records/**",
            "/library/actuator/prometheus", "/actuator/prometheus", "/library/actuator/health",       
            "/actuator/health" 
        };
    }

    private CsrfTokenRepository csrfTokenRepository() {
        CookieCsrfTokenRepository repository = CookieCsrfTokenRepository.withHttpOnlyFalse();
        repository.setCookiePath("/");
        repository.setCookieName("XSRF-TOKEN");
        return repository;
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
                          //configuration.setAllowedOrigins(List.of("*"));
                          configuration.setAllowedMethods(List.of("GET", "POST", "DELETE", "PUT", "OPTIONS"));
                          configuration.setAllowedHeaders(List.of("Authorization",
                                                                  "Content-Type",
                                                                  "X-Requested-With",
                                                                  "Accept",
                                                                  "Origin",
                                                                  "X-XSRF-TOKEN",
                                                                  "X-CSRF-TOKEN" ));
                          configuration.setExposedHeaders(List.of("Content-Disposition",
                                                                  "Content-Length",
                                                                  "X-Custom-Header",
                                                                  "X-XSRF-TOKEN" ));
                          configuration.setAllowCredentials(true);
                          configuration.setMaxAge(1800L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                                        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


                       
    private String[] publicEndpoints() {
        return new String[]{ "/login", "/logout", "/change-password", "/error", "/register", "/clear-error-message", "/icon/**", "/css/**",
                             "/library/actuator/prometheus", "/actuator/prometheus", "/library/actuator/health","/actuator/health"};
    }

    private String[] privateEndpoint(){
        return new String[]{ "/swagger-ui/index.html",  "/**", "/", "/index", "/library", "/library/**",
                             "/records/**", "/library/swagger-ui/index.html", "/library/app/index.html" };
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }


   /**  @Bean
    public FilterRegistrationBean<CsrfCookieFilter> csrfCookieFilter() {
        FilterRegistrationBean<CsrfCookieFilter> registration = new FilterRegistrationBean<>();
                                                 registration.setFilter(new CsrfCookieFilter());
                                                 registration.addUrlPatterns("/*");
        return registration;
    }

    public class CsrfCookieFilter extends OncePerRequestFilter {
        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
            CsrfToken csrfToken = (CsrfToken) request.getAttribute("_csrf");
            if (csrfToken != null) {
                System.out.println("CSRF Token: " + csrfToken.getToken());
            }
            filterChain.doFilter(request, response);
        }
    }*/



    /**@Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize -> authorize.anyRequest()
            .permitAll())
            .csrf(csrf -> csrf.disable());
        return http.build();
    }*/

}
