package com.itrail.library.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import com.itrail.library.aspect.logger.ExecuteEndpointLog;
import com.itrail.library.request.record.CardRecordRequest;
import com.itrail.library.request.record.CreateCardRecordRequest;
import com.itrail.library.response.CardRecordResponse;
import com.itrail.library.response.RecordReponse;
import com.itrail.library.rest.IRecordController;
import com.itrail.library.service.CardRecordService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class RecordController implements IRecordController {

    private final CardRecordService cardRecordService;

    @ExecuteEndpointLog
    @Override
    public ResponseEntity<CardRecordResponse> getRecordByCard( CardRecordRequest cardRecordRequest ) {
        return new ResponseEntity<>(  cardRecordService.getRecordByCard( cardRecordRequest) , HttpStatus.OK );
    }

    @ExecuteEndpointLog
    @Override
    public ResponseEntity<RecordReponse> createRecord(CreateCardRecordRequest createCardRecordRequest) {
        return new ResponseEntity<>( cardRecordService.createCardRecord( createCardRecordRequest ), HttpStatus.CREATED ); 
    }
    
}
