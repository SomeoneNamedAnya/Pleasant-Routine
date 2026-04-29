package org.app.sharing.controller;

import lombok.RequiredArgsConstructor;
import org.app.auth.SecurityContext;
import org.app.sharing.dto.CreateSharingCardRequest;
import org.app.sharing.dto.SharingCardDto;
import org.app.sharing.service.SharingService;
import org.app.user.domain.User;
import org.app.user.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/discovery/sharing")
@RequiredArgsConstructor
public class SharingController {

    private final SharingService sharingService;
    private final UserService userService;

    private User currentUser() {
        Long userId = SecurityContext.getCurrentUserId();
        return userService.getUserInfo(userId);
    }

    @PostMapping
    public ResponseEntity<SharingCardDto> create(
            @RequestBody CreateSharingCardRequest request
    ) {
        return ResponseEntity.ok(
                sharingService.create(currentUser(), request));
    }

    @PostMapping("/{cardId}/claim")
    public ResponseEntity<SharingCardDto> claim(
            @PathVariable Long cardId
    ) {
        return ResponseEntity.ok(
                sharingService.claim(currentUser(), cardId));
    }

    @GetMapping("/my-created")
    public ResponseEntity<Page<SharingCardDto>> myCreated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(
                sharingService.myCreated(currentUser(), page, size));
    }

    @GetMapping("/my-claimed")
    public ResponseEntity<Page<SharingCardDto>> myClaimed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(
                sharingService.myClaimed(currentUser(), page, size));
    }

    @GetMapping("/all-active")
    public ResponseEntity<Page<SharingCardDto>> allActive(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(
                sharingService.allActive(currentUser(), page, size));
    }

    @DeleteMapping("/{cardId}")
    public ResponseEntity<Void> delete(@PathVariable Long cardId) {
        sharingService.delete(currentUser(), cardId);
        return ResponseEntity.noContent().build();
    }
}