package com.itrail.library.rest;

import javax.ws.rs.core.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import com.itrail.library.domain.CardRecord;
import com.itrail.library.request.CardRecordRequest;
import com.itrail.library.request.CreateCardRecordRequest;
import com.itrail.library.response.BaseError;
import com.itrail.library.response.CardRecordResponse;
import com.itrail.library.response.RecordReponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RequestMapping( value = "records")
@Tag(name = "Записи", description = "Записи")
    @ApiResponses(value = {
        @ApiResponse( responseCode = "200", description = "Успешно",        content = { @Content( mediaType = MediaType.APPLICATION_JSON, array = @ArraySchema(schema = @Schema( implementation = CardRecord.class ))) }),
        @ApiResponse( responseCode = "400", description = "Плохой запрос ", content = { @Content( mediaType = MediaType.APPLICATION_JSON, array = @ArraySchema(schema = @Schema( implementation = BaseError.class ))) }),
        @ApiResponse( responseCode = "500", description = "Ошибка сервера", content = { @Content( mediaType = MediaType.APPLICATION_JSON, array = @ArraySchema(schema = @Schema( implementation = BaseError.class ))) })
    })
public interface IRecordController {

    //@GetMapping( value = "/{cardRecordRequest}")
    @PostMapping(value = "/users")
    @Operation( description = "Получение списка записей по карте", summary = "Получение списка записей по карте")
    public ResponseEntity<CardRecordResponse> getRecordByCard( @RequestBody CardRecordRequest cardRecordRequest );

    @PostMapping(value = "/create")
    @Operation( description = "Добавление записи карты", summary = "Добавление записи карты")
    public ResponseEntity<RecordReponse> createRecord( @RequestBody CreateCardRecordRequest createCardRecordRequest );
      
}
