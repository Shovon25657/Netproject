package com.businesschat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ChatMessageDAO {

    // Insert a new chat message
    public boolean insertChatMessage(String sessionId, String sender, String message, String filePath) {
        String query = "INSERT INTO chat_messages (session_id, sender, message, file_path) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, sessionId);
            stmt.setString(2, sender);
            stmt.setString(3, message);
            stmt.setString(4, filePath); // Save the file path (NULL for text messages)
            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public List<String> getMessagesBySessionId(String sessionId) {
        String query = "SELECT sender, message, file_path, timestamp FROM chat_messages WHERE session_id = ?";
        List<String> messages = new ArrayList<>();
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, sessionId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String sender = rs.getString("sender");
                String message = rs.getString("message");
                String filePath = rs.getString("file_path");
                String timestamp = rs.getString("timestamp");

                if (filePath != null) {
                    messages.add("[" + timestamp + "] " + sender + " sent a file: " + filePath);
                } else {
                    messages.add("[" + timestamp + "] " + sender + ": " + message);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return messages;
    }


}
