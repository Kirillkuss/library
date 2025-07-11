package com.itrail.library.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import com.itrail.library.aspect.logger.ExecuteEndpointLog;
import com.itrail.library.request.card.CardFilterRequest;
import com.itrail.library.response.CardInfoResponse;
import com.itrail.library.response.CardResponseLazy;
import com.itrail.library.rest.ICardController;
import com.itrail.library.service.СardService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CardController implements ICardController {
    
    private final СardService сardService;

    @ExecuteEndpointLog
    @Override
    public ResponseEntity<CardInfoResponse> getFullInfoCardAndRecord( CardFilterRequest cardFilterRequest) {
        return new ResponseEntity<> ( сardService.getFullInfoCardAndRecord( cardFilterRequest.user(), 
                                                                            cardFilterRequest.page(), 
                                                                            cardFilterRequest.size()), HttpStatus.OK );
    }

    @Override
    public ResponseEntity<List<CardResponseLazy>> getLazyCards(int page, int size) {
         return new ResponseEntity<> ( сardService.getLazyCard(page, size), HttpStatus.OK );
    }

    
}
