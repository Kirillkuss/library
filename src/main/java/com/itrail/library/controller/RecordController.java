package com.itrail.library.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import com.itrail.library.aspect.logger.ExecuteEndpointLog;
import com.itrail.library.aspect.metrics.TrackMetrics;
import com.itrail.library.request.record.CardRecordRequest;
import com.itrail.library.request.record.CreateCardRecordRequest;
import com.itrail.library.response.BaseResponse;
import com.itrail.library.response.CardRecordResponse;
import com.itrail.library.response.RecordResponse;
import com.itrail.library.rest.IRecordController;
import com.itrail.library.service.CardRecordService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class RecordController implements IRecordController {

    private final CardRecordService cardRecordService;

    @TrackMetrics(layer = "controller", tags = "endpoint=getRecordByCard")
    @ExecuteEndpointLog
    @Override
    public ResponseEntity<CardRecordResponse> getRecordByCard( CardRecordRequest cardRecordRequest ) {
        return new ResponseEntity<>(  cardRecordService.getRecordByCard( cardRecordRequest) , HttpStatus.OK );
    }

    @TrackMetrics(layer = "controller", tags = "endpoint=createRecord")
    @ExecuteEndpointLog
    @Override
    public ResponseEntity<BaseResponse<RecordResponse>> createRecord(CreateCardRecordRequest createCardRecordRequest) {
        return new ResponseEntity<>( cardRecordService.createCardRecord( createCardRecordRequest ), HttpStatus.CREATED ); 
    }

    @TrackMetrics(layer = "controller", tags = "endpoint=getLazyRecord")
    @ExecuteEndpointLog
    @Override
    public ResponseEntity<List<RecordResponse>> getLazyRecord(int page, int size) {
        return new ResponseEntity<>( cardRecordService.getAllRecord( page, size ), HttpStatus.OK ); 
    }
    
}
