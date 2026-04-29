package org.app.note.repository;

import org.app.note.PersonalNote;
import org.app.note.TagPersonalNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagPersonalNoteRepository extends JpaRepository<TagPersonalNote, Long> {

    void deleteAllByNote(PersonalNote note);

    List<TagPersonalNote> findAllByNote(PersonalNote note);
}