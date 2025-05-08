package com.example.ttcs_jobwebsite.websocket;

import com.example.ttcs_jobwebsite.token.TokenHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WebSocketService extends TextWebSocketHandler {
    public static final Map<Long, List<WebSocketSession>> webSocketSessions = new HashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        Long userId = getUserIdBy(session);
        List<WebSocketSession> webSocketSessionOfCurrentRequest;
        if (webSocketSessions.containsKey(userId)) {
            webSocketSessionOfCurrentRequest = webSocketSessions.get(userId);
        }
        else {
            webSocketSessionOfCurrentRequest = new ArrayList<>();
        }
        webSocketSessionOfCurrentRequest.add(session);
        webSocketSessions.put(userId, webSocketSessionOfCurrentRequest);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        Long userId = getUserIdBy(session);
        List<WebSocketSession> webSocketSessionsOfCurrentRequest = webSocketSessions.get(userId);
        webSocketSessionsOfCurrentRequest.remove(session);
        if (webSocketSessionsOfCurrentRequest.isEmpty()) {
            webSocketSessions.remove(userId);
        } else {
            webSocketSessions.put(userId, webSocketSessionsOfCurrentRequest);
        }
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        super.handleMessage(session, message);
    }

    public void sendNotificationCount(Long userId, int count) {
        List<WebSocketSession> sessions = webSocketSessions.get(userId);
        if (sessions != null) {
            for (WebSocketSession session : sessions) {
                try {
                    Map<String, Object> data = Map.of("type", "notiCount", "count", count);
                    String json = objectMapper.writeValueAsString(data);
                    session.sendMessage(new TextMessage(json));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Long getUserIdBy(WebSocketSession session){
        String token = session.getUri().getQuery();
        String tokenBearer = "Bearer " + token;
        return TokenHelper.getUserIdFromToken(tokenBearer);
    }
}
