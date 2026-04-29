package org.app.task.dto;

import java.util.List;

public record CreateTaskRequest(
        String title,
        String description,
        String deadline,
        String type,
        Long roomId,
        List<Long> performerIds,
        List<Long> watcherIds
) {}