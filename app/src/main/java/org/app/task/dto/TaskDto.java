package org.app.task.dto;

import java.util.List;

public record TaskDto(
        Long id,
        String title,
        String description,
        String createAt,
        String deadline,
        String type,
        String status,
        Long roomId,
        UserShortDto creator,
        List<UserShortDto> performers,
        List<WatcherDto> watchers
) {}