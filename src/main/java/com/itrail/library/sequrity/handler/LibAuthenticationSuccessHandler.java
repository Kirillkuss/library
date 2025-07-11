package com.itrail.library.sequrity.handler;
 
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class LibAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final LibAuthenticationFailureHandler libAuthenticationFailureHandler;

    @Override
    public void onAuthenticationSuccess( HttpServletRequest httpServletRequest,
                                         HttpServletResponse httpServletResponse,
                                         Authentication authentication ) throws IOException {
        HttpSession session = httpServletRequest.getSession();
        if (session != null) {
            libAuthenticationFailureHandler.convertToAuthenticated( session.getId(), (String) httpServletRequest.getSession().getAttribute("AUTH_USERNAME") );
            session.removeAttribute("error");
        }
        httpServletResponse.sendRedirect("/library/index.html");
        //httpServletResponse.sendRedirect("/swagger-ui/index.html");
    }
}