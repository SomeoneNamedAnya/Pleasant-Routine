package org.app.task.dto;

public record WatcherDto(
        Long id,
        String name,
        String surname,
        String photoLink,
        boolean approved
) {}
