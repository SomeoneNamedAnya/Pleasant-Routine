package org.app.auth.dto;

public record ChangePasswordRequest(
        String email,
        String oldPassword,
        String newPassword
) {}
