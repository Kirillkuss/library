package com.itrail.library.rest;

import java.util.Iterator;
import java.util.List;
import javax.ws.rs.core.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import com.itrail.library.config.redis.domain.Session;
import com.itrail.library.request.CreateUserRequest;
import com.itrail.library.response.BaseError;
import com.itrail.library.response.BaseResponse;
import com.itrail.library.response.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@RequestMapping( value = "users")
@Tag(name = "1. Пользователи", description = "Пользователи")
    @ApiResponses(value = {
        @ApiResponse( responseCode = "200", description = "Успешно",        content = { @Content( mediaType = MediaType.APPLICATION_JSON, array = @ArraySchema(schema = @Schema( implementation = UserResponse.class ))) }),
        @ApiResponse( responseCode = "400", description = "Плохой запрос ", content = { @Content( mediaType = MediaType.APPLICATION_JSON, array = @ArraySchema(schema = @Schema( implementation = BaseError.class ))) }),
        @ApiResponse( responseCode = "500", description = "Ошибка сервера", content = { @Content( mediaType = MediaType.APPLICATION_JSON, array = @ArraySchema(schema = @Schema( implementation = BaseError.class ))) })
    })
public interface IUserController {

    @GetMapping(value = "/lazy/{page}/{size}")
    @Operation( description = "Получение списка пользователей", summary = "Получение списка пользователей")
    public ResponseEntity<List<UserResponse>> getUsers( int page, int size ) ;

    @PostMapping(value = "/create")
    @Operation( description = "Добавление пользователя", summary = "Добавление пользователя")
    public ResponseEntity<BaseResponse> createUser( @RequestBody CreateUserRequest createUserRequest ) ;

    @GetMapping(value = "/sessions")
    @Operation( description = "Сессии", summary = "Сессии")
    public ResponseEntity<Iterator<Session>> getSessions() ;

    @DeleteMapping(value = "/sessions/remove")
    @Operation( description = "Удалить текущую сессию из Redis", summary = "Удалить текущую сессию из Redis")
    public ResponseEntity<BaseResponse> deleteUserSession( HttpServletRequest httpServletRequest ) throws IllegalAccessException;
    
    @GetMapping(value = "/{param}")
    @Operation( description = "Поиск пользователей", summary = "Поиск пользователей")
    public ResponseEntity<List<UserResponse>> getUsersForUI( String param ) ;
}
