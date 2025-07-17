package com.itrail.library.request.rtsp;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Запрос на создание записи в .mp4")
public record RtspRequest( 
    @Schema(description = "Путь",  example = "rtsp://localhost:8554/second") String path, 
    @Schema(description = "Время", example = "20")                           int duration ) {
    
}
