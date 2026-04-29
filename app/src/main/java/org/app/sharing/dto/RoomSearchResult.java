package org.app.sharing.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomSearchResult {
    private Long   id;
    private String number;
    private Long   dormitoryId;
    private String dormitoryName;
    private String publicPhotoLink;
}