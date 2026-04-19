package org.app.auth.dto;

public record AuthResponse(String accessToken, String refreshToken, Boolean hasToChangePassword) {}