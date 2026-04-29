package org.app.sharing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SharingCardDto {
    private Long    id;
    private String  title;
    private String  description;
    private String  photoLink;
    private Long    creatorId;
    private String  creatorName;
    private Long    roomId;
    private String  roomNumber;
    private Long    dormitoryId;
    private Long    claimedById;
    private String  claimedByName;
    private Boolean isActive;
    private String  createdAt;
}