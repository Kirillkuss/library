package com.itrail.library.service;

import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.itrail.library.domain.LogEntry;
import com.itrail.library.repository.LogEntryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogService {

    private final LogEntryRepository logEntryRepository;

    public List<LogEntry> getLogsJpa(int page, int size) {
        Pageable pageable = PageRequest.of( page - 1, size );
        if( page <= 0 ) throw new IllegalArgumentException("Значение страницы должно быть больше нуля!");
        if( size <= 0 ) throw new IllegalArgumentException("Значение размера страницы должно быть больше нуля!");
        return logEntryRepository.findByOrderByIdDesc( pageable );
    }
    
}
