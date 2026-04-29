package com.coursework.pleasantroutineui.services

import com.coursework.pleasantroutineui.domain.auth.ChangePasswordRequest
import com.coursework.pleasantroutineui.dto.auth.AuthResponse
import com.coursework.pleasantroutineui.dto.auth.LoginRequest
import com.coursework.pleasantroutineui.dto.auth.RefreshRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {


    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): AuthResponse

    @POST("auth/logout")
    suspend fun logout(@Body body: RefreshRequest)

    @POST("auth/refresh")
    suspend fun refresh(@Body body: Map<String, String>): AuthResponse

    @POST("auth/change-password")
    suspend fun changePassword(@Body body: ChangePasswordRequest): AuthResponse
}