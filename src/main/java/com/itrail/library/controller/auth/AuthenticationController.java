package com.itrail.library.controller.auth;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import javax.imageio.ImageIO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.itrail.library.request.AuthRequest;
import com.itrail.library.request.CreateUserRequest;
import com.itrail.library.response.UserResponse;
import com.itrail.library.rest.auth.IAuthentication;
import com.itrail.library.service.UserService;
import com.itrail.library.service.auth.AuthService;
import com.itrail.library.service.mail.EmailService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import java.awt.image.BufferedImage;
@Controller
@RequiredArgsConstructor
public class AuthenticationController implements IAuthentication {
    
    private final AuthService authService;
    private final UserService userService;
    private final EmailService emailService;
    
    @Override
    public String securecode() {
        return "securecode";
    }

    @Override
    public String login() {
        return "login";
    }

    @Override
    public String login(String error, Model model) {
	    if (error != null) {
            switch (error) {
                case "multiple_session":
                    model.addAttribute("error", "У вас уже есть активная сессия. Вы можете войти только с одного устройства.");
                    break;
                case "blocked":
                    model.addAttribute("error", "Пользователь заблокирован, попробуйте позже!");
                    break;
                case "session_expired":
                    model.addAttribute("error", "Ваша сессия истекла. Пожалуйста, войдите снова.");
                    break;
                default:
                    model.addAttribute("error", "Произошла неизвестная ошибка. Код: " + error);
                    break;
            }
        }
        return "login";
    }

    @Override
    public String login( AuthRequest authRequest, RedirectAttributes redirectAttributes) {
        try{
            String login = authService.authUser( authRequest.username(), authRequest.password() );
            if ( login != null ) {
                RequestContextHolder.currentRequestAttributes().setAttribute("AUTH_USERNAME", authRequest.username(), RequestAttributes.SCOPE_SESSION);
                return "redirect:/securecode";
            }
        }catch( Exception ex ) {
            RequestContextHolder.currentRequestAttributes().setAttribute("AUTH_USERNAME", authRequest.username(), RequestAttributes.SCOPE_SESSION);
            redirectAttributes.addFlashAttribute("error", ex.getMessage() );
        }
        return "redirect:/login";
    }

    @Override
    public String clearErrorMessage( HttpServletRequest httpServletRequest ) {
        httpServletRequest.getSession().removeAttribute("error");
        return "redirect:/securecode"; 
    }

    @Override
    public String register() {
        return "register";
    }

    @Override
    public String registerGetQr( CreateUserRequest createUserRequest,
                                 HttpServletRequest httpServletRequest,
                                 RedirectAttributes redirectAttributes,
                                 HttpServletResponse httpServletResponse) {
        try {
            UserResponse userResponse = userService.createUserRegister( createUserRequest );
            if (userResponse != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                BufferedImage qrImage = authService.generateQR(userResponse.login());
                if (qrImage != null) {
                    ImageIO.write(qrImage, "png", baos);
                    String qrBase64 = Base64.getEncoder().encodeToString( baos.toByteArray() );
                    redirectAttributes.addFlashAttribute("qrImageBase64", qrBase64 );
                    redirectAttributes.addFlashAttribute("message", "Отсканируйте QR в Google Authenticator");
                }
            }
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/register";
    }

    @Override
    public String changePassword() {
        return "change-password";
    }

    @Override
    public String requestPasswordChange( String userinfo, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        try{
            String email = emailService.sendNewPasswordToEmail( userinfo );
            redirectAttributes.addFlashAttribute("message", "Новый пароль отправлен на вашу почту: " + email );
        }catch( Exception ex ){
            redirectAttributes.addFlashAttribute("error", ex.getMessage() );
        }
        return "redirect:/change-password"; 
    }

}
