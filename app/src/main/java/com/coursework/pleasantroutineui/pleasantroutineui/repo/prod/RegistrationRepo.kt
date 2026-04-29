package com.coursework.pleasantroutineui.repo.prod

import com.coursework.pleasantroutineui.config.TokenManager
import com.coursework.pleasantroutineui.domain.auth.ChangePasswordRequest
import com.coursework.pleasantroutineui.dto.auth.AuthResponse
import com.coursework.pleasantroutineui.dto.auth.LoginRequest
import com.coursework.pleasantroutineui.services.AuthApiService
import javax.inject.Inject

class RegistrationRepo @Inject constructor(
    private val api: AuthApiService,
    private val tokenManager: TokenManager
) : IRegistrationRepo {

    override suspend fun register(login: String, password: String): Result<AuthResponse> {
        return try {
            val response = api.login(LoginRequest(login, password))


            if (response.accessToken != null && response.refreshToken != null) {
                tokenManager.saveTokens(response.accessToken, response.refreshToken)
            }

            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun changePassword(
        email: String,
        oldPassword: String,
        newPassword: String
    ): Result<AuthResponse> {
        return try {
            val response = api.changePassword(
                ChangePasswordRequest(email, oldPassword, newPassword)
            )

            if (response.accessToken != null && response.refreshToken != null) {
                tokenManager.saveTokens(response.accessToken, response.refreshToken)
            }

            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}