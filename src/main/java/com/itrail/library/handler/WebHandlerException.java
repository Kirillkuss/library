package com.itrail.library.handler;

import java.util.NoSuchElementException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import com.itrail.library.response.BaseError;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class WebHandlerException  extends ResponseEntityExceptionHandler{

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<BaseError> errBaseResponse( Throwable ex ){
        return ResponseEntity.internalServerError()
                             .body( new BaseError( 500, ex.getMessage() ));
    }

    @ExceptionHandler( NoSuchElementException.class )
    public ResponseEntity<BaseError> errBaseResponse( NoSuchElementException ex ){
        return ResponseEntity.status( HttpStatus.NOT_FOUND )
                             .body( new BaseError( 404, ex.getMessage() ));
    }

    @ExceptionHandler( IllegalArgumentException.class )
    public ResponseEntity<BaseError> errBaseResponse( IllegalArgumentException ex ){
        return ResponseEntity.badRequest()
                             .body( new BaseError( 400, ex.getMessage() ));
    }
    
}
