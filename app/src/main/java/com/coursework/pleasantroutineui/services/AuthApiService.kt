package com.coursework.pleasantroutineui.services

import com.coursework.pleasantroutineui.dto.auth.AuthResponse
import com.coursework.pleasantroutineui.dto.auth.LoginRequest
import com.coursework.pleasantroutineui.dto.auth.RefreshRequest
import com.coursework.pleasantroutineui.dto.auth.RegisterRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    @POST("/auth/register")
    suspend fun register(@Body info: RegisterRequest): AuthResponse

    @POST("/auth/login")
    suspend fun login(@Body info: LoginRequest): AuthResponse

    @POST("/auth/logout")
    suspend fun logout(@Body info: RefreshRequest)



}