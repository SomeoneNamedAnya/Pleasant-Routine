package org.app.dormitory.controller;

import lombok.RequiredArgsConstructor;
import org.app.auth.SecurityContext;
import org.app.dormitory.service.DiscoveryService;
import org.app.sharing.dto.PagedNews;
import org.app.sharing.dto.RoomSearchResult;
import org.app.sharing.dto.UserSearchResult;
import org.app.user.domain.User;
import org.app.user.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/discovery")
@RequiredArgsConstructor
public class DiscoveryController {

    private final DiscoveryService discoveryService;
    private final UserService userService;

    private User currentUser() {
        Long userId = SecurityContext.getCurrentUserId();
        return userService.getUserInfo(userId);
    }

    @GetMapping("/people")
    public ResponseEntity<Page<UserSearchResult>> searchPeople(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(
                discoveryService.searchPeople(currentUser(), id, name, page, size));
    }

    @GetMapping("/rooms")
    public ResponseEntity<Page<RoomSearchResult>> searchRooms(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String number,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(
                discoveryService.searchRooms(currentUser(), id, number, page, size));
    }

    @GetMapping("/news")
    public ResponseEntity<PagedNews> getNews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(
                discoveryService.getNews(currentUser(), page, size));
    }
}