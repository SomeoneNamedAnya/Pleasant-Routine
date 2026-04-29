package org.app.room;

import lombok.RequiredArgsConstructor;
import org.app.auth.SecurityContext;
import org.app.auth.config.LinkDto;
import org.app.room.domain.RoomInfo;
import org.app.room.service.RoomService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    @PostMapping("/info_by_id")
    public ResponseEntity<?> getRoomInfoById(@RequestParam(required = false) String roomIdStr) {
        try {
            Long roomId = Long.parseLong(roomIdStr);
            RoomInfo roomInfo = roomService.getRoomInfoById(roomId);
            return ResponseEntity.status(200).body(roomInfo.toRoomDto());

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(500).build();
        }

    }

    @PostMapping("/update_private_info")
    public ResponseEntity<?> updatePrivateInfo(
            @RequestParam(required = false) String text
    ) {
        try {
            Long userId = SecurityContext.getCurrentUserId();
            roomService.updatePrivateInfo(userId, text);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/update_public_info")
    public ResponseEntity<?> updatePublicInfo(
            @RequestParam(required = false) String text
    ) {
        try {
            Long userId = SecurityContext.getCurrentUserId();
            roomService.updatePublicInfo(userId, text);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/photo")
    public ResponseEntity<?> setPhoto(@RequestParam("file") MultipartFile file) {
        try {
            Long userId = SecurityContext.getCurrentUserId();
            String url = roomService.setPhoto(userId, file);
            return ResponseEntity.status(200).body(new LinkDto(url));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

}
