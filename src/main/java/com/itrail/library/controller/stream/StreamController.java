package com.itrail.library.controller.stream;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import com.github.kokorin.jaffree.StreamType;
import com.github.kokorin.jaffree.ffmpeg.FFmpeg;
import com.github.kokorin.jaffree.ffmpeg.PipeOutput;
import com.itrail.library.request.rtsp.RtspRequest;
import com.itrail.library.service.rtsp.StreamRtsp;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
public class StreamController {

    /**@GetMapping(value = "/video/live/{filename}", produces = "video/mp4")
    public void streamVideo(HttpServletResponse response, @PathVariable String filename) throws IOException {
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
        response.setHeader("Connection", "close");
        
        FFmpeg.atPath()
            .addArguments("-rtsp_transport", "tcp")
            .addArguments("-fflags", "+genpts+igndts") 
            .addArguments("-analyzeduration", "10M") 
            .addArguments("-i", "rtsp://localhost:8554/" + filename)
            .addArguments("-c:v", "libx264")
            .addArguments("-preset", "veryfast") 
            .addArguments("-crf", "23")  
            .addArguments("-c:a", "aac") 
            .addArguments("-b:a", "128k")  
            .addArguments("-f", "mp4")
            .addArguments("-max_muxing_queue_size", "1024")  
            .addArguments("-movflags", "frag_keyframe+empty_moov")  
            .addArguments("-reset_timestamps", "1")  
            .addOutput(PipeOutput.pumpTo(response.getOutputStream()))
            .execute();
    }*/
    
    private static final Map<String, Process> activeProcesses = new ConcurrentHashMap<>();

    @GetMapping(value = "/video/live/{filename}", produces = "video/mp4")
    public void streamVideo(HttpServletResponse response, 
                          @PathVariable String filename,
                          HttpServletRequest request) throws IOException {
        
        String sessionId = request.getSession().getId();
        
        stopPreviousProcess(sessionId);

        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
        response.setHeader("Connection", "close");

        String[] ffmpegCommand = {
            "ffmpeg",
            "-y",
            "-rtsp_transport", "tcp",
            "-fflags", "+genpts+igndts",
            "-analyzeduration", "10M",
            "-i", "rtsp://localhost:8554/" + filename,
            "-c:v", "libx264",
            "-preset", "veryfast",
            "-tune", "zerolatency", 
            "-crf", "23",
            "-c:a", "aac",
            "-b:a", "128k",
            "-f", "mp4",
            "-movflags", "frag_keyframe+empty_moov+faststart", 
            "-reset_timestamps", "1",
            "-max_muxing_queue_size", "1024",
            "-" 
        };

        ProcessBuilder processBuilder = new ProcessBuilder(ffmpegCommand);
        processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);
        
        try {
            Process process = processBuilder.start();
            activeProcesses.put(sessionId, process);
            new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getErrorStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.err.println("FFmpeg: " + line);
                    }
                } catch (IOException e) {
                    if (!process.isAlive()) {
                        activeProcesses.remove(sessionId);
                    }
                }
            }).start();
            try (InputStream ffmpegOutput = process.getInputStream();
                 OutputStream httpOutput = response.getOutputStream()) {
                
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = ffmpegOutput.read(buffer)) != -1) {
                    if (Thread.currentThread().isInterrupted()) {
                        break;
                    }
                    httpOutput.write(buffer, 0, bytesRead);
                    httpOutput.flush();
                }
            }
            
        } catch (IOException e) {
            throw new IOException("ERROR when processing video strem ", e);
        }
    }

    private void stopPreviousProcess(String sessionId) {
        Process previousProcess = activeProcesses.get(sessionId);
        if (previousProcess != null && previousProcess.isAlive()) {
            try {
                previousProcess.destroy();
                if (!previousProcess.waitFor(2, TimeUnit.SECONDS)) {
                    previousProcess.destroyForcibly();
                }
                System.out.println("Stoppted previous precess with id: " + sessionId);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("ERROR when stopped process : " + e.getMessage());
            } finally {
                activeProcesses.remove(sessionId);
            }
        }
    }
}