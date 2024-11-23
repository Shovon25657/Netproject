package com.businesschat;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new ChatWebSocketHandler(), "/chat/{sessionId}")
                .setAllowedOrigins("*") // Allow connections from any origin; adjust for production
                .addInterceptors(new HandshakeInterceptor() {
                    @Override
                    public boolean beforeHandshake(
                            org.springframework.http.server.ServerHttpRequest request,
                            org.springframework.http.server.ServerHttpResponse response,
                            org.springframework.web.socket.WebSocketHandler wsHandler,
                            Map<String, Object> attributes) throws Exception {
                        // Extract sessionId from the WebSocket URL
                        String path = request.getURI().getPath();
                        String sessionId = path.substring(path.lastIndexOf("/") + 1);
                        attributes.put("sessionId", sessionId); // Add sessionId to session attributes
                        System.out.println("Handshake initiated for Session ID: " + sessionId);
                        return true;
                    }

                    @Override
                    public void afterHandshake(
                            org.springframework.http.server.ServerHttpRequest request,
                            org.springframework.http.server.ServerHttpResponse response,
                            org.springframework.web.socket.WebSocketHandler wsHandler,
                            Exception exception) {
                        // Post-handshake logic, if needed
                        if (exception == null) {
                            System.out.println("Handshake successful.");
                        } else {
                            System.err.println("Handshake failed: " + exception.getMessage());
                        }
                    }
                });
    }
}
