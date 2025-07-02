package com.itrail.library.controller;

import java.util.Iterator;
import java.util.List;
import java.util.stream.StreamSupport;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import com.itrail.library.aspect.logger.ExecuteEndpointLog;
import com.itrail.library.config.redis.domain.Session;
import com.itrail.library.config.redis.repository.SessionRepository;
import com.itrail.library.request.CreateUserRequest;
import com.itrail.library.response.BaseResponse;
import com.itrail.library.response.UserResponse;
import com.itrail.library.rest.IUserController;
import com.itrail.library.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController implements IUserController{

    private final UserService userService;
    private final SessionRepository sessionRepository;

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
        return new ResponseEntity<>( StreamSupport.stream( sessionRepository.findAll().spliterator(), false)
                                                  .filter( f ->  f != null )
                                                  .iterator(), HttpStatus.OK );
    }

}
