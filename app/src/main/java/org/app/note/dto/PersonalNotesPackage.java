package org.app.note.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.app.user.dto.UserDto;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonalNotesPackage {
    private List<PersonalNoteDto> allNotes;
    private List<String> allTags;
    private List<UserDto> allUser;
}