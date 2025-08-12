package com.itrail.library.sequrity.provider;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import com.itrail.library.domain.User;
import com.itrail.library.repository.UserRepository;
import com.itrail.library.service.auth.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
@Component
@RequiredArgsConstructor
public class LibAuthenticationProvider implements AuthenticationProvider{

    private final AuthService        authService;
    private final UserRepository userRepository;
    private final HttpServletRequest httpServletRequest;
     
    @Override
    public Authentication authenticate( Authentication authentication ) throws AuthenticationException {
        String login = authentication.getName();
        String password = authentication.getCredentials().toString();
        User user = userRepository.findByLogin(authService.authUser( login, password )).orElse(null );

        return new UsernamePasswordAuthenticationToken( user, password, user.getRoles()
                                                                        .stream()
                                                                        .map( role -> {
                                                                            return new SimpleGrantedAuthority("ROLE_" + role.getName()) ;
                                                                        }).toList());
    }

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}
}   