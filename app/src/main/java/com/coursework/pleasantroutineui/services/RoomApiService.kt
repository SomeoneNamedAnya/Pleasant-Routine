package com.coursework.pleasantroutineui.services

import com.coursework.pleasantroutineui.domain.RoomInfo
import com.coursework.pleasantroutineui.domain.User
import com.coursework.pleasantroutineui.dto.LinkDto
import com.coursework.pleasantroutineui.dto.SignedLinkResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface RoomApiService {
    @GET("/room/info")
    suspend fun getSelfRoomInfo(): RoomInfo

    @POST("/link/sign")
    suspend fun signedLink(@Body request: LinkDto): SignedLinkResponse

}