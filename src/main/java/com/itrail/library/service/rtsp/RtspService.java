package com.itrail.library.service.rtsp;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
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
                //executePowerShellScriptForHEVC( rtspRequest.path(), rtspRequest.duration() );
                recordRtspStreamForHevc(  rtspRequest.path(), rtspRequest.duration(), saveDirectory );
            }
        }
        return new BaseResponse( 200, "success");
    }
    /**
     * Запись RTSP для H264
     * @param path - RTSP
     * @throws Exception
     */
    private void executeRecordForH264(RtspRequest rtspRequest) throws Exception {
        try {
            File dir = new File(saveDirectory);
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    throw new Exception("Failed to create directory " + saveDirectory);
                }
            }
            if (!dir.canWrite()) {
                throw new Exception("No write permissions for directory " + saveDirectory);
            }
            if (rtspRequest.path() == null || rtspRequest.path().trim().isEmpty()) {
                throw new Exception("RTSP path is empty");
            }
            if (!rtspRequest.path().startsWith("rtsp://")) {
                throw new Exception("Invalid RTSP URL format");
            }
            if (rtspRequest.duration() <= 5) {
                throw new Exception("Invalid duration!");
            }
            
            String outputFilePath = saveDirectory + "/" + rtspRequest.path().substring(rtspRequest.path().lastIndexOf('/') + 1) + ".mp4";
            String[] ffmpegCommand = {
                "ffmpeg",
                "-y",
                "-rtsp_transport", "tcp",
                "-fflags", "+genpts+igndts",  
                "-analyzeduration", "10M",
                "-i", rtspRequest.path(),
                "-c:v", "libx264",
                "-preset", "veryfast",
                "-crf", "23",
                "-c:a", "aac",
                "-b:a", "128k",
                "-t", String.valueOf(rtspRequest.duration()),
                "-f", "mp4",
                "-max_muxing_queue_size", "1024", 
                outputFilePath
            };

            ProcessBuilder processBuilder = new ProcessBuilder(ffmpegCommand);
                           processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            StreamRtsp outputGobbler = new StreamRtsp( process.getInputStream() );
                      outputGobbler.start();

            try {
                if (rtspRequest.duration() > 0) {
                    boolean finished = process.waitFor(rtspRequest.duration(), TimeUnit.SECONDS);
                    if (!finished) {
                        try (OutputStream stdin = process.getOutputStream()) {
                            stdin.write('q');
                            stdin.flush();
                        }
                        process.waitFor(5, TimeUnit.SECONDS);
                    }
                } else {
                    process.waitFor();
                }
            } finally {
                if (process.isAlive()) {
                    process.destroy();
                }
            }

            File outputFile = new File(outputFilePath);
            if (!outputFile.exists() || outputFile.length() == 0) {
                throw new Exception("Output file was not created or is empty");
            }
            log.info("Video recorded successfully! Saved to: " + outputFilePath);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

    /**
     * Выполнение скрипта через PowerShell
     * @param rtspUrl - RTSP
     * @param duration - время записи в секундах
     */
    private void executePowerShellScriptForHEVC( String rtspUrl, int duration ) {
        try {
            Path tempScript = Files.createTempFile("script-", ".ps1");
            Files.copy(scriptResource.getInputStream(), tempScript, StandardCopyOption.REPLACE_EXISTING);
            executeScript(tempScript.toAbsolutePath().toString(), rtspUrl, duration, saveDirectory );
            Files.deleteIfExists( tempScript) ;
            log.info( "Video recorded successfully! Saved to: " + saveDirectory  );
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

    /**
     * Запись видео 
     * @param rtspUrl  - RTSP поток
     * @param duration - время записи в сек
     * @param outputDir - путь, куда сохранять
     * @throws IOException
     * @throws InterruptedException
     * @throws TimeoutException
     */
    public void recordRtspStreamForHevc( String rtspUrl, int duration, String outputDir ) throws IOException, InterruptedException, TimeoutException {
        String[] parts = rtspUrl.split("/");
        String endpointName = parts[parts.length - 1];
        Path tempFile = Paths.get( outputDir, "temp_" + endpointName + ".hevc" );
        Path mkvFile = Paths.get( outputDir, endpointName + ".mkv" );
        Path mp4File = Paths.get( outputDir, endpointName + ".mp4" );
            
        Files.createDirectories(Paths.get( outputDir ));
        deleteIfExists(tempFile);
        deleteIfExists(mkvFile);
        deleteIfExists(mp4File);
            
            executeFfmpegProcess( duration + 10,
                       "ffmpeg", "-y", "-loglevel", "warning",
                                  "-fflags", "+genpts+igndts",
                                  "-use_wallclock_as_timestamps", "1",
                                  "-i", rtspUrl,
                                  "-t", String.valueOf( duration ),
                                  "-c:v", "copy", "-an", "-f", "hevc",
                                  tempFile.toString());
            
            if (!Files.exists(tempFile)) {
                throw new IOException("Не удалось создать временный HEVC файл");
            }
            
            executeFfmpegProcess(60, 
                                "ffmpeg", "-y", "-loglevel", "warning",
                                "-i", tempFile.toString(),
                                "-c", "copy", "-f", "matroska",
                                mkvFile.toString());
            
            if (Files.exists(mkvFile)) {
                executeFfmpegProcess( 120, 
                                    "ffmpeg", "-y",
                                        "-i", mkvFile.toString(),
                                        "-vf", "scale=1920:1080",
                                        "-c:v", "libx264", "-preset", "fast", "-crf", "23",
                                        "-c:a", "aac", "-b:a", "128k",
                                        "-f", "mp4",
                                        mp4File.toString());
                deleteIfExists(tempFile);
                deleteIfExists(mkvFile);
            }
            log.info( "Video recorded successfully! Saved to: " + saveDirectory + "/" + endpointName + ".mp4" );
        }
    /**
     * Метод для выполнения записи 
     * @param timeoutSeconds - время 
     * @param command - список команд
     * @throws IOException
     * @throws InterruptedException
     * @throws TimeoutException
     */    
    private void executeFfmpegProcess( int timeoutSeconds, String... command ) throws IOException, InterruptedException, TimeoutException {
        ProcessBuilder pb = new ProcessBuilder( command );
                       pb.redirectErrorStream(true);
        Process process = pb.start();
        StreamRtsp outputGobbler = new StreamRtsp( process.getInputStream() );
                   outputGobbler.start();
        if (!process.waitFor(timeoutSeconds, TimeUnit.SECONDS)) {
             process.destroyForcibly();
             outputGobbler.interrupt();
             throw new TimeoutException("Таймаут выполнения команды: " + String.join(" ", command ));
        }
        int exitCode = process.exitValue();
        if (exitCode != 0) {
            throw new IOException("FFmpeg завершился с кодом ошибки: " + exitCode);
        }
    }
    /**
     * Учдаление файла
     * @param path - путь к файлу
     * @throws IOException
     */        
    private void deleteIfExists( Path path ) throws IOException {
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            log.error("Ошибка при удалении файла " + path + ": " + e.getMessage());
        }
    }
        
}
