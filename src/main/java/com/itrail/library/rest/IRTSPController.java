package com.itrail.library.rest;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.itrail.library.request.rtsp.RtspRequest;
import com.itrail.library.response.BaseResponse;
import javax.ws.rs.core.MediaType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RequestMapping( value = "videos")
@Tag(name = "6. RTSP", description = "RTSP")
public interface IRTSPController {

    @GetMapping("/{filename}")
    @Operation( description = "Получение записи формата .mp4 полученной из RTSP", summary = "Получение записи формата .mp4 полученной из RTSP")
    @ApiResponses(value = {
        @ApiResponse( responseCode = "200", description = "Успешно",        content = { @Content(mediaType = "video/mp4") }),
        @ApiResponse( responseCode = "400", description = "Плохой запрос ", content = { @Content( mediaType = "video/mp4" ) }),
        @ApiResponse( responseCode = "500", description = "Ошибка сервера", content = { @Content( mediaType = "video/mp4" ) })
    })
    public ResponseEntity<Resource> getReourceRtst( @PathVariable String filename );

    @PostMapping("/record")
    @Operation( description = "Создание записи в формате .mp4 из RTSP", summary = "Создание записи в формате .mp4 из RTSP")
    @ApiResponses(value = {
        @ApiResponse( responseCode = "200", description = "Успешно",        content = { @Content( mediaType = MediaType.APPLICATION_JSON) }),
        @ApiResponse( responseCode = "400", description = "Плохой запрос ", content = { @Content( mediaType = MediaType.APPLICATION_JSON) }),
        @ApiResponse( responseCode = "500", description = "Ошибка сервера", content = { @Content( mediaType = MediaType.APPLICATION_JSON) })
    })
    public ResponseEntity<BaseResponse> recordVideo( @RequestBody RtspRequest rtspRequest ) throws Exception;
    
}
