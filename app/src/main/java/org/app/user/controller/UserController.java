package org.app.user.controller;

import lombok.RequiredArgsConstructor;
import org.app.auth.SecurityContext;
import org.app.auth.config.LinkDto;
import org.app.user.domain.User;
import org.app.user.dto.AboutRequest;
import org.app.user.dto.UserDto;
import org.app.user.dto.UserRequest;
import org.app.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/self_info")
    public ResponseEntity<?> getSelfInfo() {
        Long userId = SecurityContext.getCurrentUserId();
        User user = userService.getUserInfo(userId);
        UserDto userDto = user.toDto();

        return ResponseEntity.status(200).body(userDto);
    }

    @PostMapping("/info")
    public ResponseEntity<?> getInfo(@RequestBody UserRequest req) {
        Long userId = Long.parseLong(req.getNum());
        User user = userService.getUserInfo(userId);
        UserDto userDto = user.toDto();
        return ResponseEntity.status(200).body(userDto);
    }


    @PostMapping("/about")
    public ResponseEntity<?> setAbout(@RequestBody AboutRequest req) {
        try {
            Long userId = SecurityContext.getCurrentUserId();
            userService.setAbout(userId, req.getAbout());
            return ResponseEntity.status(200).build();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(500).build();
        }

    }

    @PostMapping("/photo")
    public ResponseEntity<?> setPhoto(@RequestParam("file") MultipartFile file) {
        try {
            Long userId = SecurityContext.getCurrentUserId();



            String url = userService.setPhoto(userId, file);

            return ResponseEntity.status(200).body(new LinkDto(url));

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(500).build();
        }

    }

}
