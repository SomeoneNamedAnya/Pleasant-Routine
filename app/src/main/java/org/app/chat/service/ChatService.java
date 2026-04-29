package org.app.chat.service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.app.chat.domain.Chat;
import org.app.chat.domain.ChatParticipant;
import org.app.chat.domain.MessageInChat;
import org.app.chat.dto.ChatDto;
import org.app.chat.dto.MessageDto;
import org.app.chat.dto.ParticipantDto;
import org.app.chat.repository.ChatParticipantRepository;
import org.app.chat.repository.ChatRepository;
import org.app.chat.repository.MessageInChatRepository;
import org.app.room.domain.RoomInfo;
import org.app.user.domain.User;
import org.app.user.repository.UserUserInfoRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;


@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final MessageInChatRepository messageInChatRepository;
    private final UserUserInfoRepository userRepository;

    @Transactional
    public ChatDto getOrCreateRoomChat(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        RoomInfo room = user.getRoom();
        if (room == null) {
            throw new RuntimeException("User has no room assigned");
        }

        Chat chat = chatRepository.findByRoomId(room.getId())
                .orElseGet(() -> {
                    Chat newChat = Chat.builder()
                            .title("Комната " + room.getNumber())
                            .room(room)
                            .creator(user)
                            .startAt(Instant.now())
                            .build();
                    return chatRepository.save(newChat);
                });


        syncParticipants(chat, room);

        return toChatDto(chat);
    }

    public ChatDto getChatById(Long chatId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat not found"));
        return toChatDto(chat);
    }

    public List<ParticipantDto> getParticipants(Long chatId) {
        return chatParticipantRepository.findAllByChatId(chatId).stream()
                .map(cp -> {
                    User u = cp.getUser();
                    return new ParticipantDto(u.getId(), u.getName(), u.getSurname(), u.getPhotoLink());
                })
                .toList();
    }

    public boolean isParticipant(Long chatId, Long userId) {
        return chatParticipantRepository.existsByChatIdAndUserId(chatId, userId);
    }

    public List<MessageDto> getMessages(Long chatId) {
        return messageInChatRepository.findAllByChatIdOrderByStartDtAsc(chatId).stream()
                .map(this::toMessageDto)
                .toList();
    }

    @Transactional
    public MessageDto saveMessage(Long chatId, Long senderId, String text) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat not found"));
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        MessageInChat msg = MessageInChat.builder()
                .chat(chat)
                .user(sender)
                .message(text)
                .startDt(Instant.now())
                .build();
        msg = messageInChatRepository.save(msg);

        return toMessageDto(msg);
    }

    private void syncParticipants(Chat chat, RoomInfo room) {
        List<User> residents = room.getResidents();
        if (residents == null) return;

        for (User resident : residents) {
            if (!chatParticipantRepository.existsByChatIdAndUserId(chat.getId(), resident.getId())) {
                ChatParticipant cp = ChatParticipant.builder()
                        .chat(chat)
                        .user(resident)
                        .build();
                chatParticipantRepository.save(cp);
            }
        }
    }

    private ChatDto toChatDto(Chat chat) {
        List<ParticipantDto> parts = chatParticipantRepository.findAllByChatId(chat.getId())
                .stream()
                .map(cp -> {
                    User u = cp.getUser();
                    return new ParticipantDto(u.getId(), u.getName(), u.getSurname(), u.getPhotoLink());
                })
                .toList();

        return ChatDto.builder()
                .id(chat.getId())
                .title(chat.getTitle())
                .creatorId(chat.getCreator().getId())
                .startAt(chat.getStartAt() != null ? chat.getStartAt().toEpochMilli() : null)
                .participants(parts)
                .build();
    }

    private MessageDto toMessageDto(MessageInChat msg) {
        User u = msg.getUser();
        return MessageDto.builder()
                .id(msg.getId())
                .chatId(msg.getChat().getId())
                .senderId(u.getId())
                .senderName(u.getName())
                .senderSurname(u.getSurname())
                .senderPhotoLink(u.getPhotoLink())
                .text(msg.getMessage())
                .timestamp(msg.getStartDt().toEpochMilli())
                .build();
    }
}

