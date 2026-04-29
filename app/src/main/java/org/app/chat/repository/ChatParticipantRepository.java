package org.app.chat.repository;

import org.app.chat.domain.ChatParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, Long> {

    List<ChatParticipant> findAllByChatId(Long chatId);

    boolean existsByChatIdAndUserId(Long chatId, Long userId);

    @Query("SELECT cp FROM ChatParticipant cp WHERE cp.user.id = :userId")
    List<ChatParticipant> findAllByUserId(@Param("userId") Long userId);

    void deleteByChatIdAndUserId(Long chatId, Long userId);
}