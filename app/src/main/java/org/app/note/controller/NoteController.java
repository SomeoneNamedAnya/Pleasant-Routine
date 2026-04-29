package org.app.note.controller;

import lombok.RequiredArgsConstructor;
import org.app.auth.SecurityContext;
import org.app.note.dto.NoteDto;
import org.app.note.dto.NotesPackage;
import org.app.note.dto.ParamDto;
import org.app.note.dto.PersonalNotesPackage;
import org.app.note.service.NoteService;
import org.app.user.domain.User;
import org.app.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/note")
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;
    private final UserService userService;

    @PostMapping("/room_with_filter")
    public ResponseEntity<PersonalNotesPackage> getNotesWIthFilter(
            @RequestBody ParamDto request
    ) {
        Long userId = SecurityContext.getCurrentUserId();
        System.out.println(noteService.getNotesWithFilter(userId, request));
        return ResponseEntity.ok(
                noteService.getNotesWithFilter(userId, request)
        );
    }

    @PostMapping("/with_filter")
    public ResponseEntity<NotesPackage> getPersonalNotesWIthFilter(
            @RequestBody ParamDto request,
            @RequestParam(required = false) Boolean isPublic
    ) {
        Long userId = SecurityContext.getCurrentUserId();
        User user = userService.getUserInfo(userId);
        System.out.println( noteService.getPersonalNotesWithFilter(
                user.getRoom().getId(),
                request,
                isPublic
        ).toString());
        return ResponseEntity.ok(
                noteService.getPersonalNotesWithFilter(
                        user.getRoom().getId(),
                        request,
                        isPublic
                )
        );
    }

    @PostMapping("/public_with_filter")
    public ResponseEntity<NotesPackage> getPublicNotesWIthFilter(
            @RequestBody ParamDto request,
            @RequestParam(required = false) Boolean isPublic,
            @RequestParam String roomIdStr
    ) {
        Long roomId = Long.parseLong(roomIdStr);

        return ResponseEntity.ok(
                noteService.getPersonalNotesWithFilter(
                        roomId,
                        request,
                        isPublic
                )
        );
    }


    @PostMapping("/to_room/{id}")
    public ResponseEntity<Void> addPerson(
            @PathVariable String id
    ) {
        Long userId = SecurityContext.getCurrentUserId();
        User user = userService.getUserInfo(userId);

        noteService.addToRoomNotes(user, id);

        return ResponseEntity.ok().build();
    }


    @PostMapping("/make_public/{id}")
    public ResponseEntity<Void> makePublic(
            @PathVariable String id
    ) {
        noteService.makeRoomNotePublic(id);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/create_personal")
    public ResponseEntity<Void> createPersonal(
            @RequestBody NoteDto dto
    ) {
        Long userId = SecurityContext.getCurrentUserId();
        User user = userService.getUserInfo(userId);

        noteService.createPersonalNote(user, dto);

        return ResponseEntity.ok().build();
    }


    @PostMapping("/create_room")
    public ResponseEntity<Void> createRoom(
            @RequestBody NoteDto dto
    ) {
        Long userId = SecurityContext.getCurrentUserId();
        User user = userService.getUserInfo(userId);

        noteService.createRoomNote(user, dto);

        return ResponseEntity.ok().build();
    }


    @PutMapping("/edit_personal/{id}")
    public ResponseEntity<Void> editPersonal(
            @PathVariable String id,
            @RequestBody NoteDto dto
    ) {
        noteService.editPersonalNote(id, dto);
        return ResponseEntity.ok().build();
    }


    @PutMapping("/edit_room/{id}")
    public ResponseEntity<Void> editRoom(
            @PathVariable String id,
            @RequestBody NoteDto dto
    ) {
        noteService.editRoomNote(id, dto);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/delete_person/{id}")
    public ResponseEntity<Void> deletePerson(
            @PathVariable String id
    ) {
        noteService.removePersonalNote(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/delete_room/{id}")
    public ResponseEntity<Void> deleteRoom(
            @PathVariable String id
    ) {
        noteService.removeRoomNote(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/personal/{id}")
    public ResponseEntity<?> getPersonalNote(@PathVariable String id) {
        try {
            return ResponseEntity.ok(noteService.getPersonalNote(Long.parseLong(id)).toDto());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/room/{id}")
    public ResponseEntity<?> getRoomNote(@PathVariable String id) {
        try {
            return ResponseEntity.ok(noteService.getRoomNote(Long.parseLong(id)).toDto());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }
}