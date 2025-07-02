package com.itrail.library.sequrity.provider;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import com.itrail.library.domain.User;
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
    private final HttpServletRequest httpServletRequest;
     
    @Override
    public Authentication authenticate( Authentication authentication ) throws AuthenticationException {
        String code         = authentication.getCredentials().toString();
        HttpSession session = httpServletRequest.getSession(false);
        String username     = (session != null) ? (String) session.getAttribute("AUTH_USERNAME") : null;
        Integer parsedCode;
        if( username == null ) throw new BadCredentialsException("User empty!");
        try {
            parsedCode = Integer.parseInt( code ); 
        } catch ( NumberFormatException e ) {
            throw new BadCredentialsException("Invalid code!!!");
        }
        User user = authService.verifyTwoFactorAuth( username, parsedCode );
        return new UsernamePasswordAuthenticationToken( user, code, user.getRoles()
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