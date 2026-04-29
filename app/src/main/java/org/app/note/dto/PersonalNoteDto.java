package org.app.note.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonalNoteDto {

    private Long id;
    private String title;
    private String content;

    private Long creatorId;
    private String creatorName;

    private String createdAt;
    private String editedAt;

    private List<String> photoLinks = List.of("");
    private List<String> tags;




}