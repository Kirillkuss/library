package com.itrail.library.rest;

import java.util.List;
import javax.ws.rs.core.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.itrail.library.domain.LogEntry;
import com.itrail.library.response.BaseError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RequestMapping( value = "logs")
@Tag(name = "8. Логи", description = "Логи")
    @ApiResponses(value = {
        @ApiResponse( responseCode = "200", description = "Успешно",        content = { @Content( mediaType = MediaType.APPLICATION_JSON, array = @ArraySchema(schema = @Schema( implementation = LogEntry.class ))) }),
        @ApiResponse( responseCode = "400", description = "Плохой запрос ", content = { @Content( mediaType = MediaType.APPLICATION_JSON, array = @ArraySchema(schema = @Schema( implementation = BaseError.class ))) }),
        @ApiResponse( responseCode = "500", description = "Ошибка сервера", content = { @Content( mediaType = MediaType.APPLICATION_JSON, array = @ArraySchema(schema = @Schema( implementation = BaseError.class ))) })
    })
public interface ILogEntryController {

    @GetMapping(value = "/lazy")
    @Operation( description = "Получение списка логов", summary = "Получение списка логов")
    public ResponseEntity<List<LogEntry>> getLazyLogs( @RequestParam(required = true) int page,
                                                       @RequestParam(required = true) int size) ;
    
}
