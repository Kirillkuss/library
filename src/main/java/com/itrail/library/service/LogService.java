package com.itrail.library.service;

import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.itrail.library.aspect.metrics.TrackMetrics;
import com.itrail.library.domain.LogEntry;
import com.itrail.library.repository.LogEntryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogService {

    private final LogEntryRepository logEntryRepository;

    @TrackMetrics(layer = "service")
    public List<LogEntry> getLogsJpa(int page, int size) {
        return logEntryRepository.findByOrderByIdDesc( PageRequest.of( page - 1, size ) );
    }
    
}
