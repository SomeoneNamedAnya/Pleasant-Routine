package org.app.task.controller;

import lombok.RequiredArgsConstructor;
import org.app.auth.SecurityContext;
import org.app.task.dto.*;
import org.app.task.service.TaskService;
import org.app.user.domain.User;
import org.app.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final UserService userService;



    @PostMapping
    public ResponseEntity<TaskDto> create(@RequestBody CreateTaskRequest req) {
        Long userId = SecurityContext.getCurrentUserId();
        User creator = userService.getUserInfo(userId);
        return ResponseEntity.ok(taskService.createTask(req, creator));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTask(id));
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<TaskDto>> getByRoom(@PathVariable Long roomId) {
        return ResponseEntity.ok(taskService.getTasksByRoom(roomId));
    }


    @GetMapping("/my")
    public ResponseEntity<List<TaskDto>> getMyTasks() {
        Long userId = SecurityContext.getCurrentUserId();
        User me = userService.getUserInfo(userId);
        return ResponseEntity.ok(taskService.getTasksByRoom(me.getRoom().getId()));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TaskDto>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(taskService.getTasksByUser(userId));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<TaskDto> changeStatus(
            @PathVariable Long id,
            @RequestBody ChangeStatusRequest req
    ) {
        Long userId = SecurityContext.getCurrentUserId();
        User me = userService.getUserInfo(userId);
        return ResponseEntity.ok(taskService.changeStatus(id, req, me));
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<List<CommentDto>> getComments(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getComments(id));
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<CommentDto> addComment(
            @PathVariable Long id,
            @RequestBody CreateCommentRequest req
    ) {
        Long userId = SecurityContext.getCurrentUserId();
        User author = userService.getUserInfo(userId);
        return ResponseEntity.ok(taskService.addComment(id, req, author));
    }

    @GetMapping("/{id}/approvals")
    public ResponseEntity<List<WatcherDto>> getApprovals(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getApprovals(id));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<WatcherDto> approve(
            @PathVariable Long id,
            @RequestBody ApproveRequest req
    ) {
        Long userId = SecurityContext.getCurrentUserId();
        User watcher = userService.getUserInfo(userId);
        return ResponseEntity.ok(taskService.approve(id, req, watcher));
    }

    @GetMapping("/my-room-residents")
    public ResponseEntity<List<UserShortDto>> getMyRoomResidents() {
        Long userId = SecurityContext.getCurrentUserId();
        User me = userService.getUserInfo(userId);
        if (me.getRoom() == null) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        List<UserShortDto> residents = me.getRoom().getResidents().stream()
                .filter(u -> u.getDeletedAt() == null)
                .map(u -> new UserShortDto(
                        u.getId(),
                        u.getName(),
                        u.getSurname(),
                        u.getPhotoLink()
                ))
                .toList();
        return ResponseEntity.ok(residents);
    }
}