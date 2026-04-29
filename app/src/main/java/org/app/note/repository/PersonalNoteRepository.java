package org.app.note.repository;

import org.app.note.PersonalNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonalNoteRepository extends JpaRepository<PersonalNote, Long>,
        JpaSpecificationExecutor<PersonalNote> {
}