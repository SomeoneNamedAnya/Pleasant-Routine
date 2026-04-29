package org.app.auth.dto;

import java.util.Date;

public record RegisterRequest(
        String email,
        String name,
        String surname,
        String lastName,
        Date dateOfBirth,
        String role,
        long educationId,
        long roomId
) {}