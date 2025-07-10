package com.itrail.library.controller;

import java.util.Iterator;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import com.itrail.library.aspect.logger.ExecuteEndpointLog;
import com.itrail.library.config.redis.domain.Session;
import com.itrail.library.config.redis.service.SessionService;
import com.itrail.library.request.CreateUserRequest;
import com.itrail.library.response.BaseResponse;
import com.itrail.library.response.UserResponse;
import com.itrail.library.rest.IUserController;
import com.itrail.library.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController implements IUserController{

    private final UserService userService;
    private final SessionService sessionService;

    @ExecuteEndpointLog
    @Override
    public ResponseEntity<List<UserResponse>> getUsers( int page, int size) {
        return new ResponseEntity<>( userService.getUsers( page, size ), HttpStatus.OK );
    }

    @ExecuteEndpointLog
    @Override
    public ResponseEntity<BaseResponse> createUser(CreateUserRequest createUserRequest) {
        userService.createUser( createUserRequest );
        return new ResponseEntity<>( new BaseResponse( 201, "success"), HttpStatus.CREATED );
    }

    @Override
    public ResponseEntity<Iterator<Session>> getSessions() {
        return new ResponseEntity<>( sessionService.getSessions(), HttpStatus.OK );
    }

    @Override
    public ResponseEntity<BaseResponse> deleteUserSession( HttpServletRequest httpServletRequest) throws IllegalAccessException {
        sessionService.deleteCurrentSession( httpServletRequest );
        return new ResponseEntity<>( new BaseResponse( 200, "success"), HttpStatus.OK );
    }

}
