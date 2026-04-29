package org.app.task.dto;

public record CommentDto(
        Long id,
        String text,
        UserShortDto author,
        String createdAt
) {}