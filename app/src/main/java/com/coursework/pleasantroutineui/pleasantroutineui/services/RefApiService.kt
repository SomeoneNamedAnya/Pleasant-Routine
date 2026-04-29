package com.coursework.pleasantroutineui.services

import retrofit2.http.GET
import retrofit2.http.Path

interface RefApiService {

    @GET("/api/reference/education-program/{id}")
    suspend fun getEducationalProgram(
        @Path("id") id: Long
    ): String

    @GET("/api/reference/education-level/{id}")
    suspend fun getEducationLevel(
        @Path("id") id: Long
    ): String

    @GET("/api/reference/department/{id}")
    suspend fun getDepartment(
        @Path("id") id: Long
    ): String
}