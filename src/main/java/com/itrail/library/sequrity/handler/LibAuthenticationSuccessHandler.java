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

    @Override
    public void onAuthenticationSuccess( HttpServletRequest httpServletRequest,
                                         HttpServletResponse httpServletResponse,
                                         Authentication authentication ) throws IOException {

        //httpServletResponse.sendRedirect("/library/app/index.html");
        httpServletResponse.sendRedirect("http://localhost:4200");
        //httpServletResponse.sendRedirect("/swagger-ui/index.html");
    }
}