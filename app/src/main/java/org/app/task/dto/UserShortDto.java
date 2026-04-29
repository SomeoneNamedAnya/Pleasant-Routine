package org.app.task.dto;

public record UserShortDto(
        Long id,
        String name,
        String surname,
        String photoLink
) {}