package org.app.note.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.app.note.PersonalNote;
import org.app.note.RoomNote;
import org.app.note.TagPersonalNote;
import org.app.note.TagRoomNote;
import org.app.note.dto.*;
import org.app.note.repository.PersonalNoteRepository;
import org.app.note.repository.RoomNoteRepository;
import org.app.note.repository.TagPersonalNoteRepository;
import org.app.note.repository.TagRoomNoteRepository;
import org.app.user.domain.User;
import org.app.user.dto.UserDto;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class NoteService {

    private final RoomNoteRepository roomNoteRepository;
    private final PersonalNoteRepository personalNoteRepository;
    private final TagRoomNoteRepository tagRoomNoteRepository;
    private final TagPersonalNoteRepository tagPersonalNoteRepository;

    public PersonalNotesPackage getNotesWithFilter(Long id, ParamDto dto) {
        System.out.println(dto.getStart());
        System.out.println(dto.getEnd());
        dto.setOwner(List.of(id));

        List<PersonalNote> notes = personalNoteRepository.findAll(PersonalNoteSpecification.filter(dto));


        Set<String> tagSet = new HashSet<>();
        Set<UserDto> userSet = new HashSet<>();

        for (PersonalNote note : notes) {
            userSet.add(note.getCreator().toDto());
            for (TagPersonalNote tagRoomNote : note.getTags()) {
                tagSet.add(tagRoomNote.getTag());
            }
        }
        List<PersonalNoteDto> noteDtos = notes.stream().map(PersonalNote::toDto).toList();

        List<String> tags = new ArrayList<>(tagSet);
        List<UserDto> users = new ArrayList<>(userSet);

        return new PersonalNotesPackage(noteDtos, tags, users);
    }

    public NotesPackage getPersonalNotesWithFilter(
            Long roomId,
            ParamDto dto,
            Boolean isPublic
    ) {

        System.out.println(dto.getStart());
        System.out.println(dto.getEnd());



        List<RoomNote> notes = roomNoteRepository.findAll(
                RoomNoteSpecification.filter(dto, roomId, isPublic)
        );


        Set<String> tagSet = new HashSet<>();
        Set<UserDto> userSet = new HashSet<>();

        for (RoomNote note : notes) {
            userSet.add(note.getCreator().toDto());

            for (TagRoomNote tag : note.getTags()) {
                tagSet.add(tag.getTag());
            }
        }
        List<RoomNoteDto> noteDtos = notes.stream().map(RoomNote::toDto).toList();

        return new NotesPackage(
                noteDtos,
                new ArrayList<>(tagSet),
                new ArrayList<>(userSet)
        );
    }

    public void addToRoomNotes(User user, String noteId) {
        Long id = Long.parseLong(noteId);

        PersonalNote source = personalNoteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Note not found"));

        RoomNote note = new RoomNote();
        note.setTitle(source.getTitle());
        note.setContent(source.getContent());
        note.setIsPublic(false);
        note.setRoomId(user.getRoom().getId());
        note.setCreator(user);
        note.setCreatedAt(source.getCreatedAt());
        note.setEditedAt(Instant.now());

        RoomNote saved = roomNoteRepository.save(note);

        List<TagRoomNote> tags = source.getTags()
                .stream()
                .map(t -> {
                    TagRoomNote tag = new TagRoomNote();
                    tag.setTag(t.getTag());
                    tag.setNote(saved);
                    return tag;
                })
                .collect(Collectors.toList());

        tagRoomNoteRepository.saveAll(tags);
    }

    public void makeRoomNotePublic(String noteId) {
        Long id = Long.parseLong(noteId);

        RoomNote source = roomNoteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room note not found"));

        RoomNote note = new RoomNote();
        note.setTitle(source.getTitle());
        note.setContent(source.getContent());
        note.setIsPublic(true);
        note.setRoomId(source.getRoomId());
        note.setCreator(source.getCreator());
        note.setCreatedAt(source.getCreatedAt());
        note.setEditedAt(Instant.now());

        RoomNote saved = roomNoteRepository.save(note);

        List<TagRoomNote> tags = source.getTags()
                .stream()
                .map(t -> {
                    TagRoomNote tag = new TagRoomNote();
                    tag.setTag(t.getTag());
                    tag.setNote(saved);
                    return tag;
                })
                .collect(Collectors.toList());

        tagRoomNoteRepository.saveAll(tags);
    }

    public void createRoomNote(User user, NoteDto dto) {
        RoomNote note = new RoomNote();

        note.setTitle(dto.getTitle());
        note.setContent(dto.getContent());
        note.setIsPublic(dto.getIsPublic());
        note.setRoomId(user.getRoom().getId());
        note.setCreator(user);
        note.setCreatedAt(Instant.now());
        note.setEditedAt(Instant.now());

        RoomNote saved = roomNoteRepository.save(note);

        List<TagRoomNote> tags = dto.getTags()
                .stream()
                .map(value -> {
                    TagRoomNote tag = new TagRoomNote();
                    tag.setTag(value);
                    tag.setNote(saved);
                    return tag;
                })
                .collect(Collectors.toList());

        tagRoomNoteRepository.saveAll(tags);
    }

    public void createPersonalNote(User user, NoteDto dto) {
        PersonalNote note = new PersonalNote();

        note.setTitle(dto.getTitle());
        note.setContent(dto.getContent());
        note.setCreator(user);
        note.setCreatedAt(Instant.now());
        note.setEditedAt(Instant.now());

        PersonalNote saved = personalNoteRepository.save(note);

        List<TagPersonalNote> tags = dto.getTags()
                .stream()
                .map(value -> {
                    TagPersonalNote tag = new TagPersonalNote();
                    tag.setTag(value);
                    tag.setNote(saved);
                    return tag;
                })
                .collect(Collectors.toList());

        tagPersonalNoteRepository.saveAll(tags);
    }

    public void editRoomNote(String noteId, NoteDto dto) {
        Long id = Long.parseLong(noteId);

        RoomNote note = roomNoteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room note not found"));

        note.setTitle(dto.getTitle());
        note.setContent(dto.getContent());
        note.setIsPublic(dto.getIsPublic());
        note.setEditedAt(Instant.now());

        roomNoteRepository.save(note);

        tagRoomNoteRepository.deleteAllByNote(note);

        List<TagRoomNote> tags = dto.getTags()
                .stream()
                .map(value -> {
                    TagRoomNote tag = new TagRoomNote();
                    tag.setTag(value);
                    tag.setNote(note);
                    return tag;
                })
                .collect(Collectors.toList());

        tagRoomNoteRepository.saveAll(tags);
    }

    public void editPersonalNote(String noteId, NoteDto dto) {
        Long id = Long.parseLong(noteId);

        PersonalNote note = personalNoteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Personal note not found"));

        note.setTitle(dto.getTitle());
        note.setContent(dto.getContent());
        note.setEditedAt(Instant.now());

        personalNoteRepository.save(note);

        tagPersonalNoteRepository.deleteAllByNote(note);

        List<TagPersonalNote> tags = dto.getTags()
                .stream()
                .map(value -> {
                    TagPersonalNote tag = new TagPersonalNote();
                    tag.setTag(value);
                    tag.setNote(note);
                    return tag;
                })
                .collect(Collectors.toList());

        tagPersonalNoteRepository.saveAll(tags);
    }

    public void removeRoomNote(String noteId) {
        Long id = Long.parseLong(noteId);

        RoomNote note = roomNoteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room note not found"));

        tagRoomNoteRepository.deleteAllByNote(note);
        roomNoteRepository.delete(note);
    }

    public void removePersonalNote(String noteId) {
        Long id = Long.parseLong(noteId);

        PersonalNote note = personalNoteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Note not found"));

        tagPersonalNoteRepository.deleteAllByNote(note);
        personalNoteRepository.delete(note);
    }

    public PersonalNote getPersonalNote(Long id) {
        return personalNoteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Note not found"));
    }

    public RoomNote getRoomNote(Long id) {
        return roomNoteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room note not found"));
    }
}