package com.itrail.library.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import com.itrail.library.aspect.logger.ExecuteEndpointLog;
import com.itrail.library.request.BookFilterRequest;
import com.itrail.library.response.CardInfoResponse;
import com.itrail.library.rest.ICardController;
import com.itrail.library.service.СardService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CardController implements ICardController {
    
    private final СardService сardService;

    @ExecuteEndpointLog
    @Override
    public ResponseEntity<CardInfoResponse> getFullInfoCardAndRecord(BookFilterRequest filterRequest) {
        return new ResponseEntity<> ( сardService.getFullInfoCardAndRecord(filterRequest.fio(), filterRequest.page(), filterRequest.size()), HttpStatus.OK );
    }

    
}
