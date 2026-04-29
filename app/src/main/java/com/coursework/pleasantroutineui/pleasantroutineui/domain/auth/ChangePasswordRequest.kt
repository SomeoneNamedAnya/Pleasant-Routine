package com.coursework.pleasantroutineui.domain.auth

data class ChangePasswordRequest(
    val email: String,
    val oldPassword: String,
    val newPassword: String
)