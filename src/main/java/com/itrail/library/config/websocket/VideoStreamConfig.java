package com.itrail.library.config.websocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

@Configuration
@EnableWebSocket
public class VideoStreamConfig implements WebSocketConfigurer {

    private final Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<>());
    private final Object lock = new Object();

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new VideoHandler(), "/video/stream")
                .setAllowedOrigins("*");
    }

    @Bean
    public Thread startTcpServer() {
        Thread serverThread = new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(9999)) {
                System.out.println("TCP Server started on port 9999");
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        Socket clientSocket = serverSocket.accept();
                        handleTcpClient(clientSocket);
                    } catch (IOException e) {
                        if (!Thread.currentThread().isInterrupted()) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        serverThread.start();
        return serverThread;
    }

    private void handleTcpClient(Socket clientSocket) {
        new Thread(() -> {
            try (InputStream input = clientSocket.getInputStream()) {
                byte[] buffer = new byte[1024 * 1024]; // 1MB buffer
                int bytesRead;
                while ((bytesRead = input.read(buffer)) != -1) {
                    byte[] packet = Arrays.copyOfRange(buffer, 0, bytesRead);
                    broadcast(packet);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void broadcast(byte[] data) {
        synchronized (lock) {
            Iterator<WebSocketSession> iterator = sessions.iterator();
            while (iterator.hasNext()) {
                WebSocketSession session = iterator.next();
                try {
                    if (session.isOpen()) {
                        session.sendMessage(new BinaryMessage(data));
                    } else {
                        iterator.remove();
                    }
                } catch (IOException e) {
                    iterator.remove();
                    e.printStackTrace();
                }
            }
        }
    }

    class VideoHandler extends AbstractWebSocketHandler {
        @Override
        protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
            // Not needed for one-way streaming
        }

        @Override
        public void afterConnectionEstablished(WebSocketSession session) {
            synchronized (lock) {
                sessions.add(session);
            }
            System.out.println("New WebSocket client connected");
        }

        @Override
        public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
            synchronized (lock) {
                sessions.remove(session);
            }
            System.out.println("WebSocket client disconnected");
        }
    }
}