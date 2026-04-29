package org.app.note.repository;

import org.app.note.RoomNote;
import org.app.note.TagRoomNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagRoomNoteRepository extends JpaRepository<TagRoomNote, Long> {

    void deleteAllByNote(RoomNote note);

    List<TagRoomNote> findAllByNote(RoomNote note);
}