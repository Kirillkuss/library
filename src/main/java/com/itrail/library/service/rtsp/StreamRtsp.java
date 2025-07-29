package com.itrail.library.service.rtsp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StreamRtsp extends Thread {

    private final InputStream inputStream;
            
    public StreamRtsp(InputStream inputStream) {
        this.inputStream = inputStream;
    }
            
    @Override
    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("[FFmpeg] " + line);
            }
        } catch (IOException e) {
            System.err.println("Ошибка при чтении вывода FFmpeg: " + e.getMessage());
        }
    }
}
