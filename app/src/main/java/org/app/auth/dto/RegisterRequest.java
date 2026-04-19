package org.app.auth.dto;

import java.time.LocalDate;
import java.util.Date;

public record RegisterRequest(
        String email,
        String name,
        String surname,
        String lastName,
        Date dateOfBirth,
        String role,
        int educationId,
        int roomId
) {}