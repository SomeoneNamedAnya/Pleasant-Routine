package org.app.chat.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.app.chat.dto.MessageDto;
import org.app.chat.dto.WsIncomingMessage;
import org.app.chat.service.ChatService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final ChatService chatService;
    private final ObjectMapper objectMapper;

    private final Map<Long, Set<WebSocketSession>> chatSessions = new ConcurrentHashMap<>();

    private final Map<String, Long> sessionChatMap = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("WebSocket connected: {}", session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) throws Exception {
        Long userId = getUserId(session);
        if (userId == null) {
            session.close(CloseStatus.POLICY_VIOLATION);
            return;
        }

        String payload = textMessage.getPayload();
        WsIncomingMessage incoming = objectMapper.readValue(payload, WsIncomingMessage.class);

        Long chatId = incoming.getChatId();
        if (chatId == null) {
            return;
        }

        if (!chatService.isParticipant(chatId, userId)) {
            session.sendMessage(new TextMessage("{\"error\":\"Вы не являетесь участником этого чата\"}"));
            return;
        }

        if (incoming.getText() == null || incoming.getText().isBlank()) {
            registerSession(chatId, session);
            return;
        }

        registerSession(chatId, session);

        MessageDto saved = chatService.saveMessage(chatId, userId, incoming.getText());

        String json = objectMapper.writeValueAsString(saved);
        broadcastToChat(chatId, json);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String sessionId = session.getId();
        Long chatId = sessionChatMap.remove(sessionId);
        if (chatId != null) {
            Set<WebSocketSession> sessions = chatSessions.get(chatId);
            if (sessions != null) {
                sessions.remove(session);
                if (sessions.isEmpty()) {
                    chatSessions.remove(chatId);
                }
            }
        }
        log.info("WebSocket disconnected: {}", sessionId);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.error("WebSocket error for session {}: {}", session.getId(), exception.getMessage());
    }

    private void registerSession(Long chatId, WebSocketSession session) {
        chatSessions.computeIfAbsent(chatId, k -> new CopyOnWriteArraySet<>()).add(session);
        sessionChatMap.put(session.getId(), chatId);
    }

    private void broadcastToChat(Long chatId, String json) {
        Set<WebSocketSession> sessions = chatSessions.get(chatId);
        if (sessions == null) return;
        TextMessage msg = new TextMessage(json);
        for (WebSocketSession s : sessions) {
            if (s.isOpen()) {
                try {
                    s.sendMessage(msg);
                } catch (IOException e) {
                    log.error("Failed to send message to session {}: {}", s.getId(), e.getMessage());
                }
            }
        }
    }

    private Long getUserId(WebSocketSession session) {
        Object attr = session.getAttributes().get("userId");
        return attr instanceof Long ? (Long) attr : null;
    }
}