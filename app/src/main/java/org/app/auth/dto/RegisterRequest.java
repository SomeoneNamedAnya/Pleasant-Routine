package org.app.auth.dto;

import java.time.LocalDate;

public record RegisterRequest(
        String email,
        String password,
        String name,
        String surname,
        String lastName,
        LocalDate dateOfBirth,
        String role,
        int educationId,
        int roomId
) {}