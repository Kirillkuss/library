package com.itrail.library.repository;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.itrail.library.domain.LogEntry;

@Repository
public interface LogEntryRepository extends JpaRepository<LogEntry, Long>{
    
    List<LogEntry> findByOrderByIdDesc( Pageable pageable);

}
