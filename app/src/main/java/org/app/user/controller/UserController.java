package org.app.user.controller;

import lombok.RequiredArgsConstructor;
import org.app.auth.SecurityContext;
import org.app.user.domain.User;
import org.app.user.mapper.UserMapper;
import org.app.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("/self_info")
    public ResponseEntity<?> getSelfInfo() {
        Long userId = SecurityContext.getCurrentUserId();
        User user = userService.getUserInfo(userId);
        return ResponseEntity.ok(userMapper.toUserDto(user));
    }
}
