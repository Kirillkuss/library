package com.itrail.library.rest;

import javax.ws.rs.core.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.itrail.library.request.book.BookFilterRequest;
import com.itrail.library.request.card.CardFilterRequest;
import com.itrail.library.response.BaseError;
import com.itrail.library.response.CardInfoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RequestMapping( value = "cards")
@Tag(name = "5. Карта", description = "Карта")
    @ApiResponses(value = {
        @ApiResponse( responseCode = "200", description = "Успешно",        content = { @Content( mediaType = MediaType.APPLICATION_JSON, array = @ArraySchema(schema = @Schema( implementation = CardInfoResponse.class ))) }),
        @ApiResponse( responseCode = "400", description = "Плохой запрос ", content = { @Content( mediaType = MediaType.APPLICATION_JSON, array = @ArraySchema(schema = @Schema( implementation = BaseError.class ))) }),
        @ApiResponse( responseCode = "500", description = "Ошибка сервера", content = { @Content( mediaType = MediaType.APPLICATION_JSON, array = @ArraySchema(schema = @Schema( implementation = BaseError.class ))) })
    })
public interface ICardController {

    @PostMapping("/card")
    @Operation( description = "Получение информации по карте пользователя", summary = "Получение информации по карте пользователя")
    public ResponseEntity<CardInfoResponse> getFullInfoCardAndRecord( @RequestBody CardFilterRequest cardFilterRequest );
    
}
