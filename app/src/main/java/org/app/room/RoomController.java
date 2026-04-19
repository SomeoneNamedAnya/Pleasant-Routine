package org.app.room;

import lombok.RequiredArgsConstructor;
import org.app.auth.SecurityContext;
import org.app.room.domain.RoomInfo;
import org.app.room.service.RoomService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/room")
@RequiredArgsConstructor
public class RoomController {
    private final RoomService roomService;
    @GetMapping("/info")
    public ResponseEntity<?> getRoomInfo() {
        try {
            Long userId = SecurityContext.getCurrentUserId();
            RoomInfo roomInfo = roomService.getSelfRoomInfo(userId);
            return ResponseEntity.status(200).body(roomInfo.toRoomDto());

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(500).build();
        }

    }

}
