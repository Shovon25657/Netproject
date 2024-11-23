package com.businesschat;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.concurrent.CopyOnWriteArrayList;

@ServerEndpoint("/chat/{sessionId}")
public class ChatWebSocketServer {
    private static final CopyOnWriteArrayList<Session> sessions = new CopyOnWriteArrayList<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("sessionId") String sessionId) {
        sessions.add(session);
        System.out.println("New connection opened: Session ID = " + sessionId);
        try {
            session.getBasicRemote().sendText("Welcome to the chat!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("Message received: " + message);
        for (Session s : sessions) {
            if (s.isOpen()) {
                try {
                    s.getBasicRemote().sendText("Server Echo: " + message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
        System.out.println("Connection closed: " + session.getId());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.err.println("Error in session " + session.getId() + ": " + throwable.getMessage());
    }
}
