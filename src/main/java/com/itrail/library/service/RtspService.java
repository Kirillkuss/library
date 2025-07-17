package com.itrail.library.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.itrail.library.request.rtsp.RtspRequest;
import com.itrail.library.response.BaseResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RtspService {

    @Value("classpath:script.ps1")
    private Resource scriptResource;

    @Value("${video.save.directory:classpath:video}")
    private String saveDirectory;
    /**
     * Выполнение записи
     * @param path - путь к RTSP
     * @return BaseResponse
     * @throws Exception
     */
    public BaseResponse makeRecord( RtspRequest rtspRequest ) throws Exception{
        if ( isH264( rtspRequest.path() )){
            TimeUnit.SECONDS.sleep(10 );
            executeRecordForH264( rtspRequest );
        }else{
            if(isHEVC( rtspRequest.path() )){
                executePowerShellScriptForHEVC( rtspRequest.path(), rtspRequest.duration() );
            }
        }
        return new BaseResponse( 200, "success");
    }
    /**
     * Запись RTSP для H264
     * @param path - RTSP
     * @throws Exception
     */
    private void executeRecordForH264( RtspRequest rtspRequest  ) throws Exception{
        try {
            File dir = new File(saveDirectory  );
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    throw new Exception("Failed to create directory " + saveDirectory);
                }
            }
            if (!dir.canWrite()) {
                throw new Exception( "No write permissions for directory " + saveDirectory);
            }
            if (rtspRequest.path() == null || rtspRequest.path().trim().isEmpty()) {
                throw new Exception( "RTSP path is empty");
            }
            if (!rtspRequest.path().startsWith("rtsp://")) {
                throw new Exception( "Invalid RTSP URL format");
            }
            if(  rtspRequest.duration() <= 5 ){
                throw new Exception( "Invalid duration!");
            }

            String outputFilePath = saveDirectory +"/" + rtspRequest.path().substring(rtspRequest.path().lastIndexOf('/') + 1) + ".mp4";
		    /**String[] ffmpegCommand = {
                "ffmpeg",
                "-y",
                "-rtsp_transport", "tcp",
                "-fflags", "+genpts+igndts",  
                "-analyzeduration", "10M",
                "-i", path,
                "-c:v", "libx264",
                "-preset", "veryfast",
                "-crf", "23",
                "-c:a", "aac",
                "-b:a", "128k",
                "-t", "20",
                "-f", "mp4",
                "-max_muxing_queue_size", "1024", 
                outputFilePath
            };*/
            String[] ffmpegCommand = {
                "ffmpeg",
                "-y",
                "-rtsp_transport", "tcp",
                "-fflags", "+genpts+igndts",
                "-analyzeduration", "10M",
                "-probesize", "10M",
                "-i", rtspRequest.path(),
                
                // Видео
                "-c:v", "libx264",
                "-preset", "veryfast",
                "-crf", "23",
                "-pix_fmt", "yuv420p",
                
                // Аудио (важные исправления)
                "-ac", "2",                    
                "-ar", "44100",               
                "-c:a", "aac",
                "-b:a", "128k",
                "-strict", "experimental",     
                
                // Общие параметры
                "-t", String.valueOf(rtspRequest.duration()),
                "-f", "mp4",
                "-max_muxing_queue_size", "1024",
                "-movflags", "+faststart",    
                outputFilePath
            }; 

                ProcessBuilder processBuilder = new ProcessBuilder(ffmpegCommand);
                            processBuilder.redirectErrorStream(true);
                Process process = processBuilder.start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("FFmpeg: " + line);
                }
                boolean finished = process.waitFor(30, TimeUnit.SECONDS); 
                if (!finished) {
                    process.destroy();
                    throw new Exception("Recording stopped. File may be at: " + outputFilePath);
                }
                File outputFile = new File( outputFilePath );
                if (!outputFile.exists() || outputFile.length() == 0) {
                    throw new Exception( "Output file was not created or is empty");
                }
                log.info( "Finish record!");
                log.info( "Video recorded successfully! Saved to: " + outputFilePath );
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                throw new Exception( e.getMessage());
            }
    }

    /**
     * Выполнение скрипта через PowerShell
     * @param rtspUrl - RTSP
     * @param duration - время записи в секундах
     */
    private void executePowerShellScriptForHEVC(String rtspUrl, int duration) {
        try {
            Path tempScript = Files.createTempFile("script-", ".ps1");
            Files.copy(scriptResource.getInputStream(), tempScript, StandardCopyOption.REPLACE_EXISTING);
            executeScript(tempScript.toAbsolutePath().toString(), rtspUrl, duration, saveDirectory );
            Files.deleteIfExists( tempScript) ;
            log.info( "Video recorded successfully! Saved to: " + rtspUrl  );
        } catch (Exception e) {
            throw new RuntimeException("Failed to execute script", e);
        }
    }
    /**
     * Выполнение сприта через PowerShell
     * @param scriptPath
     * @param rtspUrl
     * @param duration
     * @param outputDir
     * @throws IOException
     * @throws InterruptedException
     */
    private void executeScript(String scriptPath, String rtspUrl, int duration, String outputDir) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder("powershell.exe",
                                                    "-ExecutionPolicy", "Bypass",
                                                    "-NoProfile",
                                                    "-NonInteractive",
                                                    "-File", scriptPath,
                                                    "-rtspUrl", rtspUrl,
                                                    "-duration", String.valueOf(duration),
                                                    "-outputDir", outputDir );
                       pb.redirectErrorStream(true);
        Process process = pb.start();
        try (BufferedReader reader = new BufferedReader( new InputStreamReader( process.getInputStream() ))) {
            reader.lines().forEach(System.out::println);
        }
        if (!process.waitFor(5, TimeUnit.MINUTES)) {
            process.destroyForcibly();
            throw new RuntimeException("Timeout");
        }
    }

    /**
     * Проверка Rtsp протокола на его размер и формат
     * @param rtspUrl - RTSP
     * @return Map String, String
     * @throws IOException
     * @throws InterruptedException
     */
    private Map<String, String> checkRtspStreamFormat(String rtspUrl) throws IOException, InterruptedException {
        Map<String, String> result = new HashMap<>();
        String[] command = { "ffprobe",
                             "-v", "error",
                             "-select_streams", "v:0",
                             "-show_entries", "stream=codec_name,width,height",
                             "-of", "default=noprint_wrappers=1:nokey=1",
                             rtspUrl };
        Process process = new ProcessBuilder(command).start();
        try (BufferedReader reader = new BufferedReader( new InputStreamReader(process.getInputStream()))) {
            String codec = reader.readLine();
            String width = reader.readLine();
            String height = reader.readLine();
            if (codec == null || width == null || height == null) {
                throw new IOException("Не удалось получить информацию о потоке");
            }
            result.put("codec", codec.trim());
            result.put("width", width.trim());
            result.put("height", height.trim());
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new IOException("Ошибка выполнения ffprobe (код: " + exitCode + ")");
            }
            return result;
        }
    }
    /**
     * Проверка RTSP на h264
     * @param rtspUrl - RTSP
     * @return boolean
     * @throws IOException
     * @throws InterruptedException
     */
    private boolean isH264(String rtspUrl) throws IOException, InterruptedException {
        Map<String, String> streamInfo = checkRtspStreamFormat(rtspUrl);
        return "h264".equalsIgnoreCase(streamInfo.get("codec"));
    }
    /**
     * Проверка RTSP на hevc
     * @param rtspUrl - RTSP
     * @return boolean
     * @throws IOException
     * @throws InterruptedException
     */
    private boolean isHEVC(String rtspUrl) throws IOException, InterruptedException {
        Map<String, String> streamInfo = checkRtspStreamFormat(rtspUrl);
        return "hevc".equalsIgnoreCase(streamInfo.get("codec"));
    }

}
