package com.itrail.library.controller.auth;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;
import javax.imageio.ImageIO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.itrail.library.request.CreateUserRequest;
import com.itrail.library.response.UserResponse;
import com.itrail.library.rest.auth.IAuthentication;
import com.itrail.library.service.UserService;
import com.itrail.library.service.auth.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import java.awt.image.BufferedImage;

@Controller
@RequiredArgsConstructor
public class AuthenticationController implements IAuthentication {
    
    private final AuthService authService;
    private final UserService userService;
    
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
    public String login( String username, String password, RedirectAttributes redirectAttributes) {
        try{
            String login = authService.authUser( username, password );
            if ( login != null ) {
                RequestContextHolder.currentRequestAttributes().setAttribute("AUTH_USERNAME", username, RequestAttributes.SCOPE_SESSION);
                return "redirect:/securecode";
            }
        }catch( Exception ex ) {
            RequestContextHolder.currentRequestAttributes().setAttribute("AUTH_USERNAME", username, RequestAttributes.SCOPE_SESSION);
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
         Set<String> roles = new HashSet<>();
                     roles.add( "ADMIN" );
         createUserRequest = new CreateUserRequest( createUserRequest.login(),
                                                    createUserRequest.password(),
                                                    createUserRequest.firstName(),
                                                    createUserRequest.secondName(),
                                                    createUserRequest.middleName(),
                                                    createUserRequest.email(),
                                                    createUserRequest.phone(),
                                                    roles );
        try{
            UserResponse userResponse = userService.createUserTwo( createUserRequest );
            redirectAttributes.addFlashAttribute("message", "Отсканируйте Qr в приложении: Google Authenticator ");
            if (userResponse != null) {
                String qrImage = "/image-access-qr?login=" + userResponse.login();
                redirectAttributes.addFlashAttribute("qrImage", qrImage);
            }
        }catch( Exception ex ){
            redirectAttributes.addFlashAttribute("error", ex.getMessage() );
        }
        return "redirect:/register"; 
    }

    @Override
    public void getQrImage(String login, HttpServletRequest request, HttpServletResponse response) {
        BufferedImage qrImage = authService.generateQR(login);
         if (qrImage != null) {
            response.setContentType(org.springframework.http.MediaType.IMAGE_PNG_VALUE);
            try (OutputStream outputStream = response.getOutputStream()) {
                ImageIO.write(qrImage, "png", outputStream);
                outputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                 }
            }
    }


    
}
