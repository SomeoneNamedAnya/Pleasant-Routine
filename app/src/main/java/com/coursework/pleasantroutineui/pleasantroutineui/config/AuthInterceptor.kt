package com.coursework.pleasantroutineui.config

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor(
    private val tokenProvider: TokenManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val token = tokenProvider.getAccessToken()

        val newRequest = if (token != null) {
            request.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            request
        }

        return chain.proceed(newRequest)
    }
}