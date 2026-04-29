package com.coursework.pleasantroutineui.repo.prod

import com.coursework.pleasantroutineui.config.TokenManager
import com.coursework.pleasantroutineui.domain.auth.ChangePasswordRequest
import com.coursework.pleasantroutineui.dto.auth.AuthResponse
import com.coursework.pleasantroutineui.dto.auth.LoginRequest
import com.coursework.pleasantroutineui.dto.auth.RefreshRequest
import com.coursework.pleasantroutineui.services.AuthApiService
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val api: AuthApiService,
    private val tokenManager: TokenManager
) {

    suspend fun login(email: String, password: String): AuthResponse {
        val response = api.login(LoginRequest(email, password))
        if (response.accessToken != null && response.refreshToken != null) {
            tokenManager.saveTokens(response.accessToken, response.refreshToken)
        }
        return response
    }

    suspend fun changePassword(
        email: String,
        oldPassword: String,
        newPassword: String
    ): AuthResponse {
        val response = api.changePassword(
            ChangePasswordRequest(email, oldPassword, newPassword)
        )
        if (response.accessToken != null && response.refreshToken != null) {
            tokenManager.saveTokens(response.accessToken, response.refreshToken)
        }
        return response
    }

    suspend fun refresh(): AuthResponse {
        val token = tokenManager.getRefreshToken()
            ?: throw RuntimeException("No refresh token")
        val response = api.refresh(mapOf("refreshToken" to token))
        if (response.accessToken != null && response.refreshToken != null) {
            tokenManager.saveTokens(response.accessToken, response.refreshToken)
        }
        return response
    }

    suspend fun logout() {
        tokenManager.getRefreshToken()?.let { token ->
            runCatching { api.logout(RefreshRequest(token)) }
        }
        tokenManager.clearTokens()
    }

    fun isLoggedIn(): Boolean = tokenManager.hasTokens()
}