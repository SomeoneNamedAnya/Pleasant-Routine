package org.app.note;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.app.note.dto.PersonalNoteDto;
import org.app.user.domain.User;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "personal_note")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PersonalNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "edited_at")
    private Instant editedAt;

    @OneToMany(mappedBy = "note")
    private List<TagPersonalNote> tags;

    public PersonalNoteDto toDto() {
        return new PersonalNoteDto(
                id,
                title,
                content,
                creator != null ? creator.getId() : null,
                creator != null ? creator.getName() + " " + creator.getSurname() : null,
                createdAt != null ? createdAt.toString() : null,
                editedAt != null ? editedAt.toString() : null,
                List.of(""),
                tags != null
                        ? tags.stream()
                        .map(TagPersonalNote::getTag)
                        .toList()
                        : List.of()
        );
    }

}
