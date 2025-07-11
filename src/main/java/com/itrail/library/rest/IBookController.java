package com.itrail.library.rest;

import java.util.List;

import javax.ws.rs.core.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.itrail.library.aspect.logger.ExecuteEndpointLog;
import com.itrail.library.domain.Author;
import com.itrail.library.domain.Book;
import com.itrail.library.request.book.BookFilterRequest;
import com.itrail.library.request.book.FreeBooksRequest;
import com.itrail.library.response.BaseError;
import com.itrail.library.response.BookFilterResponse;
import com.itrail.library.response.BookResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RequestMapping( value = "books")
@Tag(name = "3. Книги", description = "Книги")
    @ApiResponses(value = {
        @ApiResponse( responseCode = "200", description = "Успешно",        content = { @Content( mediaType = MediaType.APPLICATION_JSON, array = @ArraySchema(schema = @Schema( implementation = Book.class ))) }),
        @ApiResponse( responseCode = "400", description = "Плохой запрос ", content = { @Content( mediaType = MediaType.APPLICATION_JSON, array = @ArraySchema(schema = @Schema( implementation = BaseError.class ))) }),
        @ApiResponse( responseCode = "500", description = "Ошибка сервера", content = { @Content( mediaType = MediaType.APPLICATION_JSON, array = @ArraySchema(schema = @Schema( implementation = BaseError.class ))) })
    })
public interface IBookController {

    @GetMapping(value = "/current/{bookFilterRequest}")
    @Operation( description = "Получение списка книг по автору", summary = "Получение списка книг по автору")
    public ResponseEntity<BookFilterResponse> getBooksByAuthor( BookFilterRequest bookFilterRequest ) ;

    @GetMapping(value = "/free/{freeBooksRequest}")
    @Operation( description = "Получение свободных книг", summary = "Получение свободных книг")
    public ResponseEntity<List<BookResponse>> getFreeBooks( FreeBooksRequest freeBooksRequest ) ;

    @GetMapping(value = "/lazy/{page}/{size}")
    @Operation( description = "Получение списка книг", summary = "Получение списка книг")
    public ResponseEntity<List<BookResponse>> getLazyAuthors( int page, int size ) ;

}
