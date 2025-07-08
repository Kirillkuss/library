package com.itrail.library.rest.auth;

import javax.ws.rs.core.MediaType;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.itrail.library.request.AuthRequest;
import com.itrail.library.request.CreateUserRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface IAuthentication {

    @GetMapping(value = "securecode", produces = MediaType.APPLICATION_JSON)
    public String securecode();

    @GetMapping(value = "login", produces = MediaType.APPLICATION_JSON)
    public String login();

    @GetMapping("/login")
    public String login( @RequestParam(value = "error", required = false) String error, Model model);

    @PostMapping("/login")
    public String login( @ModelAttribute AuthRequest authRequest,
                         RedirectAttributes redirectAttributes );

    @PostMapping(value = "clear-error-message", produces = MediaType.APPLICATION_JSON)
    public String clearErrorMessage( HttpServletRequest httpServletRequest );

    @GetMapping(value = "register", produces = MediaType.APPLICATION_JSON)
    public String register();

    @PostMapping(value = "register")
    public String registerGetQr( @ModelAttribute CreateUserRequest createUserRequest,
                                                 HttpServletRequest httpServletRequest,
                                                 RedirectAttributes redirectAttributes,
                                                 HttpServletResponse httpServletResponse );

    @GetMapping(value = "change-password", produces = MediaType.APPLICATION_JSON)
    public String changePassword();

    @PostMapping(value = "change-password")
    public String requestPasswordChange( @RequestParam("userinfo") String userinfo, HttpServletRequest request, RedirectAttributes redirectAttributes );
    
}
