package org.app.room.domain;

import jakarta.persistence.*;
import lombok.*;
import org.app.room.dto.RoomDto;
import org.app.user.domain.User;

import java.util.List;

@Entity
@Table(name = "room")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String number;

    @Column(name = "dormitory_id")
    private Long dormitoryId;

    @Column(name = "public_info")
    private String publicInfo;

    @Column(name = "private_info")
    private String privateInfo;

    @Column(name = "public_photo_link")
    private String publicPhotoLink;

    @OneToMany(mappedBy = "roomId", fetch = FetchType.LAZY)
    private List<User> residents;

    public RoomDto toRoomDto() {
        return new RoomDto(
                id,
                number,
                dormitoryId,
                publicInfo,
                privateInfo,
                publicPhotoLink,
                residents.stream().map(User::toUserDto).toList()
        );
    }

}
