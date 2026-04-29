package org.app.sharing.domain;

import jakarta.persistence.*;
import lombok.*;
import org.app.ref_info.domain.Dormitory;
import org.app.room.domain.RoomInfo;
import org.app.sharing.dto.SharingCardDto;
import org.app.user.domain.User;

import java.time.Instant;

@Entity
@Table(name = "sharing_card")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SharingCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "photo_link")
    private String photoLink;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id")
    private User creator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private RoomInfo room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dormitory_id")
    private Dormitory dormitory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "claimed_by")
    private User claimedBy;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "created_at")
    private Instant createdAt;

    public SharingCardDto toDto() {
        return SharingCardDto.builder()
                .id(id)
                .title(title)
                .description(description)
                .photoLink(photoLink)
                .creatorId(creator != null ? creator.getId() : null)
                .creatorName(creator != null
                        ? creator.getSurname() + " " + creator.getName()
                        : null)
                .roomId(room != null ? room.getId() : null)
                .roomNumber(room != null ? room.getNumber() : null)
                .dormitoryId(dormitory != null ? dormitory.getId() : null)
                .claimedById(claimedBy != null ? claimedBy.getId() : null)
                .claimedByName(claimedBy != null
                        ? claimedBy.getSurname() + " " + claimedBy.getName()
                        : null)
                .isActive(isActive)
                .createdAt(createdAt != null ? createdAt.toString() : null)
                .build();
    }
}