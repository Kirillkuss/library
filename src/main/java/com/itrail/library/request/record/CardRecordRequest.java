package com.itrail.library.request.record;

import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import io.swagger.v3.oas.annotations.media.Schema;

public record CardRecordRequest(                        String user,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime finish,
    @Schema(description = "страница",  example = "1")   int page,
    @Schema(description = "размер", example = "10")     int size ) {}
