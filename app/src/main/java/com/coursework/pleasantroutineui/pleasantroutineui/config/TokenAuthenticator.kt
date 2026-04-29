package com.coursework.pleasantroutineui.config

import com.coursework.pleasantroutineui.dto.auth.RefreshRequest
import com.coursework.pleasantroutineui.services.RefreshApiService
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator(
    private val api: RefreshApiService,
    private val tokenManager: TokenManager
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {

        if (responseCount(response) >= 2) return null

        val refreshToken = tokenManager.getRefreshToken() ?: return null

        val newTokens = try {
            val refreshResponse = api.refresh(RefreshRequest(refreshToken)).execute()
            if (refreshResponse.isSuccessful) {
                refreshResponse.body()
            } else null
        } catch (e: Exception) {
            null
        }

        return newTokens?.let {
            tokenManager.saveTokens(accessToken = it.accessToken, refreshToken = it.refreshToken)

            response.request.newBuilder()
                .header("Authorization", "Bearer ${it.accessToken}")
                .build()
        }
    }

    private fun responseCount(response: Response): Int {
        var result = 1
        var res = response.priorResponse
        while (res != null) {
            result++
            res = res.priorResponse
        }
        return result
    }
}