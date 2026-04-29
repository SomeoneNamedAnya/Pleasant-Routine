package org.app.chat.controller;

import lombok.RequiredArgsConstructor;
import org.app.auth.SecurityContext;
import org.app.chat.dto.ChatDto;
import org.app.chat.dto.MessageDto;
import org.app.chat.dto.ParticipantDto;
import org.app.chat.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @GetMapping("/room")
    public ResponseEntity<ChatDto> getRoomChat() {
        Long userId = SecurityContext.getCurrentUserId();
        ChatDto chat = chatService.getOrCreateRoomChat(userId);
        return ResponseEntity.ok(chat);
    }

    @GetMapping("/{chatId}/participants")
    public ResponseEntity<List<ParticipantDto>> getParticipants(@PathVariable Long chatId) {
        return ResponseEntity.ok(chatService.getParticipants(chatId));
    }

    @GetMapping("/{chatId}/messages")
    public ResponseEntity<List<MessageDto>> getMessages(@PathVariable Long chatId) {
        Long userId = SecurityContext.getCurrentUserId();
        if (!chatService.isParticipant(chatId, userId)) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(chatService.getMessages(chatId));
    }
}