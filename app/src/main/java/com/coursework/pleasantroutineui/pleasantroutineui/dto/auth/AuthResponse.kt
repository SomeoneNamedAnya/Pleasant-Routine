package com.coursework.pleasantroutineui.dto.auth

data class AuthResponse(
    val accessToken: String?,
    val refreshToken: String?,
    val hasToChangePassword: Boolean
)
