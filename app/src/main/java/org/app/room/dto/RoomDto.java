package org.app.room.dto;

import jakarta.persistence.*;
import lombok.*;
import org.app.user.domain.User;
import org.app.user.dto.UserDto;

import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomDto {

        private Long id;
        private String number;
        private Long dormitoryId;
        private String publicInfo;
        private String privateInfo;
        private String publicPhotoLink;
        private List<UserDto> residents;

}
