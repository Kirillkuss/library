package com.itrail.library.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.itrail.library.aspect.metrics.TrackMetrics;
import com.itrail.library.domain.LogEntry;
import com.itrail.library.rest.ILogEntryController;
import com.itrail.library.service.LogService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class LogEntryController implements ILogEntryController {

    private final LogService logService;
    
    @TrackMetrics(layer = "controller")
    @Override
    public ResponseEntity<List<LogEntry>> getLazyLogs(int page, int size) {
        return ResponseEntity.ok().body( logService.getLogsJpa( page, size));
    }


    
}
