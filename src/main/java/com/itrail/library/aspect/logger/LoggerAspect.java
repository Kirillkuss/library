package com.itrail.library.aspect.logger;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.NoSuchElementException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import com.itrail.library.domain.LogEntry;
import com.itrail.library.domain.User;
import com.itrail.library.repository.LogEntryRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LoggerAspect {

	private final LogEntryRepository logEntryRepository;
    
    @Around( value = "@annotation(com.itrail.library.aspect.logger.ExecuteMethodLog )")
    public Object executeMethodLog(  ProceedingJoinPoint proceedingJoinPoint )  throws Throwable{
        StopWatch stopWatch = new StopWatch();
                  stopWatch.start();
                  stopWatch.stop();
        Object proceed = proceedingJoinPoint.proceed();
        MethodSignature methodSignature = ( MethodSignature ) proceedingJoinPoint.getSignature();
        log.info( new StringBuilder().append("Method name: ").append( methodSignature.getMethod().getName() ).append(", ")
                                     .append( "Execution time: " + stopWatch.getTotalTimeMillis() + " ms")
                                     .toString());
        return proceed;
    }

    @Around( value = "@annotation(com.itrail.library.aspect.logger.ExecuteEndpointLog)")
    public Object executeEndpointLog(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        StopWatch stopWatch = new StopWatch();
                  stopWatch.start();
        HttpServletRequest request   = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();
        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
        String methodName               = methodSignature.getMethod().getName();
        String className                = proceedingJoinPoint.getTarget().getClass().getSimpleName();
        Object[] args                   = proceedingJoinPoint.getArgs();
        String uri = request.getRequestURI();

        Object proceed;
        String status = "SUCCESS";
        String errorMessage = null;
        User user = null;

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if( auth != null && auth.getPrincipal() instanceof User) {
             user = (User) auth.getPrincipal(); 
        }
        int responseStatus = HttpServletResponse.SC_OK;
        if (response != null) {
            responseStatus = response.getStatus();
        }
        
        try {
            proceed = proceedingJoinPoint.proceed();
            stopWatch.stop();
            if ( proceed instanceof ResponseEntity ) {
                responseStatus = (( ResponseEntity<?> ) proceed).getStatusCode().value();
            }
        }catch( Exception ex ){
            stopWatch.stop();
            status = "ERROR";
            errorMessage = ex.getMessage();
            if (ex instanceof NoSuchElementException ){
                responseStatus = 404;
            }else if(  ex instanceof IllegalArgumentException ){
                responseStatus = 400;
            }else{
                responseStatus = 500;
            }
            if (response != null) {
                response.setStatus(responseStatus);
            }
            
            throw ex;
        } finally {
            uri = URLDecoder.decode(uri, StandardCharsets.UTF_8.name());   
            logEntryRepository.save( LogEntry.builder()
                                             .serverTime (LocalDateTime.now())
                                             .level( status.equals("ERROR") ? "ERROR" : "INFO")
                                             .logger( className + "." + methodName )
                                             .message( "REST")
                                             .exception( errorMessage )
                                             .username( user != null ? user.getLogin() : null)
                                             .requestMethod( request.getMethod() )
                                             .requestUri( uri )
                                             .responseStatus( response.getStatus() )
                                             .executeTime(stopWatch.getTotalTimeMillis())
                                             .build() );
        }
        log.info(" [Controller Log] Method: {} | Time: {} ms | Args: {} | Status: {} ", methodName, stopWatch.getTotalTimeMillis(), Arrays.toString(args), response.getStatus() );             
        return proceed;
    }
}


