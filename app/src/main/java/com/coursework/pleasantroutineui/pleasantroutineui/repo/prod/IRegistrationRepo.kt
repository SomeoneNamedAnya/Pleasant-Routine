package com.coursework.pleasantroutineui.repo.prod

import com.coursework.pleasantroutineui.dto.auth.AuthResponse

interface IRegistrationRepo {
    suspend fun register(login: String, password: String): Result<AuthResponse>
    suspend fun changePassword(
        email: String,
        oldPassword: String,
        newPassword: String
    ): Result<AuthResponse>
}