package com.businesschat;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.concurrent.CopyOnWriteArrayList;

public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
    private final ChatMessageDAO chatMessageDAO = new ChatMessageDAO(); // DAO for database operations

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        String sessionId = (String) session.getAttributes().get("sessionId"); // Retrieve sessionId from attributes
        System.out.println("New connection established. Session ID: " + sessionId);

        // Welcome the user
        session.sendMessage(new TextMessage("Welcome to the chat! Session ID: " + sessionId));

        // Retrieve chat history and send it to the client
        session.sendMessage(new TextMessage("Previous messages:"));
        chatMessageDAO.getMessagesBySessionId(sessionId).forEach(message -> {
            try {
                session.sendMessage(new TextMessage(message));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            String sessionId = (String) session.getAttributes().get("sessionId");
            String payload = message.getPayload();

            if (payload.startsWith("FILE:")) {
                // Handle file message
                String filePath = saveFile(payload.substring(5)); // Decode and save the file
                if (filePath != null) {
                    // Save the file path to the database
                    boolean isSaved = chatMessageDAO.insertChatMessage(sessionId, "customer", null, filePath);
                    if (isSaved) {
                        broadcastToClients("Customer sent a file: " + filePath);
                    } else {
                        session.sendMessage(new TextMessage("Error: Unable to save file metadata."));
                    }
                } else {
                    session.sendMessage(new TextMessage("Error: Unable to save file."));
                }
            } else {
                // Handle regular text messages
                boolean isSaved = chatMessageDAO.insertChatMessage(sessionId, "customer", payload, null);
                if (isSaved) {
                    broadcastToClients("Customer: " + payload);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            session.sendMessage(new TextMessage("Error processing your message."));
        }
    }



    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) throws Exception {
        sessions.remove(session);
        String sessionId = (String) session.getAttributes().get("sessionId");
        System.out.println("Connection closed for Session ID: " + sessionId);
    }

    private String saveFile(String fileData) {
        try {
            // Decode base64 file data
            byte[] decodedBytes = java.util.Base64.getDecoder().decode(fileData);

            // Ensure the "uploads" folder exists
            String fileName = "uploads/" + System.currentTimeMillis() + ".png"; // Save as PNG
            java.nio.file.Path filePath = java.nio.file.Paths.get(fileName);
            java.nio.file.Files.createDirectories(filePath.getParent()); // Create directory if it doesn't exist

            // Write file to the path
            java.nio.file.Files.write(filePath, decodedBytes);
            return fileName; // Return relative path
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid Base64 data: " + e.getMessage());
            return null; // Handle malformed Base64 data gracefully
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Log and return null on error
        }
    }



    private void broadcastToClients(String message) {
        for (WebSocketSession s : sessions) {
            if (s.isOpen()) {
                try {
                    s.sendMessage(new TextMessage(message));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
