package com.itrail.library.rest;

import java.util.List;
import javax.ws.rs.core.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.itrail.library.aspect.logger.ExecuteEndpointLog;
import com.itrail.library.domain.Author;
import com.itrail.library.response.BaseError;
import com.itrail.library.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RequestMapping( value = "authors")
@Tag(name = "2. Авторы", description = "авторы")
    @ApiResponses(value = {
        @ApiResponse( responseCode = "200", description = "Успешно",        content = { @Content( mediaType = MediaType.APPLICATION_JSON, array = @ArraySchema(schema = @Schema( implementation = BaseResponse.class ))) }),
        @ApiResponse( responseCode = "400", description = "Плохой запрос ", content = { @Content( mediaType = MediaType.APPLICATION_JSON, array = @ArraySchema(schema = @Schema( implementation = BaseError.class ))) }),
        @ApiResponse( responseCode = "500", description = "Ошибка сервера", content = { @Content( mediaType = MediaType.APPLICATION_JSON, array = @ArraySchema(schema = @Schema( implementation = BaseError.class ))) })
    })
public interface IAuthorController {

    @GetMapping(value = "/{fio}")
    @Operation( description = "Получение списка авторов по ФИО", summary = "Получение списка авторов по ФИО")
    public ResponseEntity<List<Author>> getAuthors( @Parameter( description = "ФИО", example = "first" ) String fio ) ;
    
}
