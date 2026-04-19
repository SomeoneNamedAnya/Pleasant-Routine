package com.coursework.pleasantroutineui.services

import com.coursework.pleasantroutineui.dto.auth.AuthResponse
import com.coursework.pleasantroutineui.dto.auth.RefreshRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface RefreshApiService {
    @POST("/refresh")
    fun refresh(@Body info: RefreshRequest): Call<AuthResponse>
}