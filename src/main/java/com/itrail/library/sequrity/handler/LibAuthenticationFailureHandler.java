package com.itrail.library.sequrity.handler;

import java.io.IOException;
import java.util.Optional;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import com.itrail.library.config.redis.repository.SessionRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.itrail.library.config.redis.domain.Session;

@Slf4j
@Component
@RequiredArgsConstructor
public class LibAuthenticationFailureHandler implements AuthenticationFailureHandler {


    @Override
    public void onAuthenticationFailure( HttpServletRequest request, HttpServletResponse response,
                                         AuthenticationException exception) throws IOException, ServletException {
        request.getSession().setAttribute("error", exception.getMessage());
        response.sendRedirect("/library/login?error=true");
    }

}