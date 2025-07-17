package com.itrail.library.controller;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import com.itrail.library.response.BaseResponse;
import com.itrail.library.rest.IRTSPController;
import lombok.RequiredArgsConstructor;
import com.itrail.library.service.RtspService;

@Controller
@RequiredArgsConstructor
public class RTSPController implements IRTSPController {

    private final RtspService rtspService;

    @Value("${video.save.directory:classpath:video}")
    private String VIDEO_DIR;

    @Override
    public ResponseEntity<Resource> getReourceRtst(String filename) {
        try {
            Path videoPath = Paths.get(VIDEO_DIR).resolve(filename);
            Resource videoResource = new UrlResource(videoPath.toUri());

            if (videoResource.exists() || videoResource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, "video/mp4")
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + videoResource.getFilename() + "\"")
                        .body(videoResource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Override
    public ResponseEntity<BaseResponse> recordVideo(String path) throws Exception {
        return ResponseEntity.ok().body( rtspService.makeRecord( path ));
    }
    
}
