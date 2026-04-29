package org.app.task.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.app.room.domain.RoomInfo;
import org.app.room.repository.RoomRepository;
import org.app.task.dto.*;
import org.app.task.entity.*;
import org.app.task.enums.TaskStatus;
import org.app.task.enums.TaskType;
import org.app.task.repository.*;
import org.app.user.domain.User;
import org.app.user.repository.UserUserInfoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepo;
    private final TaskCreatorRepository creatorRepo;
    private final TaskPerformerRepository performerRepo;
    private final TaskWatcherRepository watcherRepo;
    private final TaskCommentRepository commentRepo;
    private final TaskApprovalRepository approvalRepo;
    private final UserUserInfoRepository userRepo;
    private final RoomRepository roomRepo;

    private final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                    .withZone(ZoneId.systemDefault());

    @Transactional
    public TaskDto createTask(CreateTaskRequest req, User creator) {

        RoomInfo room = roomRepo.findById(req.roomId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Room not found"));

        TaskEntity task = TaskEntity.builder()
                .title(req.title())
                .description(req.description())
                .createAt(Instant.now())
                .deadline(Instant.parse(req.deadline()))
                .type(TaskType.valueOf(req.type()))
                .status(TaskStatus.OPEN)
                .room(room)
                .build();
        task = taskRepo.save(task);


        creatorRepo.save(TaskCreatorEntity.builder()
                .task(task).user(creator).build());

        final TaskEntity savedTask = task;


        for (Long perfId : req.performerIds()) {
            User perf = userRepo.findById(perfId)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Performer not found: " + perfId));
            performerRepo.save(TaskPerformerEntity.builder()
                    .task(savedTask).user(perf).build());
        }

        for (Long wId : req.watcherIds()) {
            User w = userRepo.findById(wId)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Watcher not found: " + wId));
            watcherRepo.save(TaskWatcherEntity.builder()
                    .task(savedTask).user(w).build());
            approvalRepo.save(TaskApprovalEntity.builder()
                    .task(savedTask)
                    .watcher(w)
                    .approved(false)
                    .createdAt(Instant.now())
                    .build());
        }

        return toTaskDto(savedTask);
    }

    public TaskDto getTask(Long taskId) {
        TaskEntity task = taskRepo.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Task not found"));
        return toTaskDto(task);
    }

    public List<TaskDto> getTasksByRoom(Long roomId) {
        return taskRepo.findByRoomId(roomId).stream()
                .map(this::toTaskDto)
                .toList();
    }

    public List<TaskDto> getTasksByUser(Long userId) {
        Set<Long> taskIds = new LinkedHashSet<>();
        creatorRepo.findByUserId(userId)
                .forEach(c -> taskIds.add(c.getTask().getId()));
        performerRepo.findByUserId(userId)
                .forEach(p -> taskIds.add(p.getTask().getId()));
        watcherRepo.findByUserId(userId)
                .forEach(w -> taskIds.add(w.getTask().getId()));

        return taskIds.stream()
                .map(id -> taskRepo.findById(id).orElse(null))
                .filter(Objects::nonNull)
                .map(this::toTaskDto)
                .toList();
    }

    public List<TaskDto> getMyTasks(User currentUser) {
        return getTasksByUser(currentUser.getId());
    }

    public List<CommentDto> getComments(Long taskId) {
        return commentRepo.findByTaskIdOrderByCreatedAtAsc(taskId).stream()
                .map(this::toCommentDto)
                .toList();
    }

    @Transactional
    public CommentDto addComment(Long taskId, CreateCommentRequest req, User author) {
        TaskEntity task = taskRepo.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Task not found"));

        TaskCommentEntity comment = TaskCommentEntity.builder()
                .task(task)
                .user(author)
                .comment(req.text())
                .createdAt(Instant.now())
                .build();
        comment = commentRepo.save(comment);
        return toCommentDto(comment);
    }

    @Transactional
    public TaskDto changeStatus(Long taskId, ChangeStatusRequest req, User currentUser) {
        TaskEntity task = taskRepo.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Task not found"));

        TaskStatus newStatus = TaskStatus.valueOf(req.newStatus());

        if (newStatus == TaskStatus.DONE) {
            validateApprovals(task);
        }

        task.setStatus(newStatus);
        task = taskRepo.save(task);
        return toTaskDto(task);
    }

    private void validateApprovals(TaskEntity task) {
        List<TaskWatcherEntity> watchers = watcherRepo.findByTaskId(task.getId());
        if (watchers.isEmpty()) return;

        long approvedCount = approvalRepo.countByTaskIdAndApprovedTrue(task.getId());

        if (task.getType() == TaskType.ALL_MUST_APPROVE) {
            if (approvedCount < watchers.size()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Не все наблюдатели подтвердили. " +
                                approvedCount + " из " + watchers.size());
            }
        } else if (task.getType() == TaskType.ANY_MUST_APPROVE) {
            if (approvedCount == 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Ни один наблюдатель не подтвердил");
            }
        }
    }

    @Transactional
    public WatcherDto approve(Long taskId, ApproveRequest req, User currentUser) {
        TaskEntity task = taskRepo.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Task not found"));

        if (task.getStatus() != TaskStatus.IN_REVIEW) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Аппрув доступен только в статусе 'На проверке'");
        }

        TaskApprovalEntity approval = approvalRepo
                .findByTaskIdAndWatcherId(taskId, currentUser.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Вы не являетесь наблюдателем этой задачи"));

        approval.setApproved(req.approved());
        approval.setCreatedAt(Instant.now());
        approvalRepo.save(approval);

        return new WatcherDto(
                currentUser.getId(),
                currentUser.getName(),
                currentUser.getSurname(),
                currentUser.getPhotoLink(),
                approval.getApproved()
        );
    }

    public List<WatcherDto> getApprovals(Long taskId) {
        return approvalRepo.findByTaskId(taskId).stream()
                .map(a -> new WatcherDto(
                        a.getWatcher().getId(),
                        a.getWatcher().getName(),
                        a.getWatcher().getSurname(),
                        a.getWatcher().getPhotoLink(),
                        a.getApproved()
                ))
                .toList();
    }

    private TaskDto toTaskDto(TaskEntity task) {
        UserShortDto creatorDto = creatorRepo.findByTaskId(task.getId())
                .map(c -> toUserShort(c.getUser()))
                .orElse(null);

        List<UserShortDto> performerDtos = performerRepo.findByTaskId(task.getId())
                .stream().map(p -> toUserShort(p.getUser())).toList();

        List<TaskApprovalEntity> approvals = approvalRepo.findByTaskId(task.getId());
        Map<Long, Boolean> approvalMap = approvals.stream()
                .collect(Collectors.toMap(
                        a -> a.getWatcher().getId(),
                        TaskApprovalEntity::getApproved,
                        (a, b) -> a));

        List<WatcherDto> watcherDtos = watcherRepo.findByTaskId(task.getId())
                .stream()
                .map(w -> new WatcherDto(
                        w.getUser().getId(),
                        w.getUser().getName(),
                        w.getUser().getSurname(),
                        w.getUser().getPhotoLink(),
                        approvalMap.getOrDefault(w.getUser().getId(), false)
                )).toList();

        return new TaskDto(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                formatter.format(task.getCreateAt()),
                formatter.format(task.getDeadline()),
                task.getType().name(),
                task.getStatus().name(),
                task.getRoom().getId(),
                creatorDto,
                performerDtos,
                watcherDtos
        );
    }

    private UserShortDto toUserShort(User u) {
        return new UserShortDto(u.getId(), u.getName(), u.getSurname(), u.getPhotoLink());
    }

    private CommentDto toCommentDto(TaskCommentEntity c) {
        return new CommentDto(
                c.getId(),
                c.getComment(),
                toUserShort(c.getUser()),
                formatter.format(c.getCreatedAt())
        );
    }
}