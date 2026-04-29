package org.app.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageDto {
    private Long id;
    private Long chatId;
    private Long senderId;
    private String senderName;
    private String senderSurname;
    private String senderPhotoLink;
    private String text;
    private Long timestamp;
}