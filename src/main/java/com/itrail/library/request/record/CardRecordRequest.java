package com.itrail.library.request.record;

import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;

public record CardRecordRequest(                                                    
                                                                                    String user,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)                             LocalDateTime start,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)                             LocalDateTime finish,
    @Min(value = 1, message = "Значение страницы должно быть больше нуля!")
    @Schema(description = "страница",  example = "1")                               int page,
    @Min(value = 1, message = "Значение размера страницы должно быть больше нуля!")
    @Schema(description = "размер", example = "10")                                 int size ) {}
