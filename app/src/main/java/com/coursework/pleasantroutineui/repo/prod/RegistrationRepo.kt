package com.coursework.pleasantroutineui.repo.prod

import com.coursework.pleasantroutineui.config.TokenManager
import com.coursework.pleasantroutineui.dto.auth.LoginRequest
import com.coursework.pleasantroutineui.services.AuthApiService
import javax.inject.Inject

class RegistrationRepo @Inject constructor(
    private val api: AuthApiService,
    private val tokenManager: TokenManager
) : IRegistrationRepo {

    override suspend fun register(login: String, password: String): Result<Unit> {

        return try {

            val response = api.login(
                LoginRequest(email = login, password = password)
            )
            println(response)
            println(response.accessToken)
            println(response.refreshToken)

            tokenManager.saveTokens(
                response.accessToken,
                response.refreshToken
            )

            Result.success(Unit)

        } catch (e: Exception) {
            println(e.message)
            Result.failure(e)
        }
    }
}