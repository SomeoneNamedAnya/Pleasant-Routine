package org.app.note;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.app.note.dto.RoomNoteDto;
import org.app.user.domain.User;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "room_note")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoomNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "is_public")
    private Boolean isPublic;

    @Column(name = "room_id")
    private Long roomId;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "edited_at")
    private Instant editedAt;

    @OneToMany(mappedBy = "note")
    private List<TagRoomNote> tags;

    public RoomNoteDto toDto() {
        return new RoomNoteDto(
                id,
                title,
                content,
                isPublic,
                roomId,
                creator != null ? creator.getId() : null,
                creator != null ? creator.getName() + " " + creator.getSurname() : null,
                createdAt != null ? createdAt.toString() : null,
                editedAt != null ? editedAt.toString() : null,
                List.of(""),
                tags != null
                        ? tags.stream()
                        .map(TagRoomNote::getTag)
                        .toList()
                        : List.of()
        );
    }
}
