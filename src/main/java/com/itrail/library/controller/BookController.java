package com.itrail.library.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import com.itrail.library.aspect.logger.ExecuteEndpointLog;
import com.itrail.library.request.book.BookFilterRequest;
import com.itrail.library.request.book.FreeBooksRequest;
import com.itrail.library.response.BookFilterResponse;
import com.itrail.library.response.BookResponse;
import com.itrail.library.rest.IBookController;
import com.itrail.library.service.BookService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class BookController implements IBookController {

    private final BookService bookService;

    @ExecuteEndpointLog
    @Override
    public ResponseEntity<BookFilterResponse> getBooksByAuthor( BookFilterRequest bookFilterRequest ) {
        return new ResponseEntity<> ( bookService.getBooksByAuthor( bookFilterRequest ), HttpStatus.OK );
    }

    @Override
    public ResponseEntity<List<BookResponse>> getFreeBooks(FreeBooksRequest freeBooksRequest) {
        return new ResponseEntity<> ( bookService.getFreeBooks( freeBooksRequest ), HttpStatus.OK );
    }
    
}
