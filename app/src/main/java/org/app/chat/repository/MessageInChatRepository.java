package org.app.chat.repository;

import org.app.chat.domain.MessageInChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageInChatRepository extends JpaRepository<MessageInChat, Long> {

    List<MessageInChat> findAllByChatIdOrderByStartDtAsc(Long chatId);
}
