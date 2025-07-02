package com.itrail.library.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import com.itrail.library.aspect.logger.ExecuteEndpointLog;
import com.itrail.library.domain.Author;
import com.itrail.library.rest.IAuthorController;
import com.itrail.library.service.AuthorService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AuthorController implements IAuthorController{

    private final AuthorService authorService;

    @ExecuteEndpointLog
    @Override
    public ResponseEntity<List<Author>> getAuthors( String fio ) {
        return new ResponseEntity<> ( authorService.getAuthors(fio ), HttpStatus.OK );
    }
    
}
