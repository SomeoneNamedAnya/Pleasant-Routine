package org.app.note;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tag_room_note")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TagRoomNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tag;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "note_id")
    private RoomNote note;

    public TagRoomNote(String tag) {
        this.tag = tag;
    }
    public TagRoomNote copy() {
        TagRoomNote tag = new TagRoomNote();
        tag.setTag(this.tag);
        return tag;
    }
}
