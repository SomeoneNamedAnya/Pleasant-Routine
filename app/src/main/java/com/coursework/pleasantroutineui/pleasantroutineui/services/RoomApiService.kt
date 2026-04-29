package com.coursework.pleasantroutineui.services

import com.coursework.pleasantroutineui.domain.RoomInfo
import com.coursework.pleasantroutineui.domain.User
import com.coursework.pleasantroutineui.dto.LinkDto
import com.coursework.pleasantroutineui.dto.SignedLinkResponse
import com.coursework.pleasantroutineui.dto.user.AboutRequest
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface RoomApiService {
    @GET("/room/info")
    suspend fun getSelfRoomInfo(): RoomInfo

    @POST("/link/sign")
    suspend fun signedLink(@Body request: LinkDto): SignedLinkResponse

    @POST("/room/update_private_info")
    suspend fun updatePrivateInfo(
        @Query("text") text: String?
    )

    @POST("/room/update_public_info")
    suspend fun updatePublicInfo(
        @Query("text") text: String?
    )

    @POST("/room/info_by_id")
    suspend fun getRoomInfoById(@Query("roomIdStr") roomId: String?): RoomInfo

    @Multipart
    @POST("/room/photo")
    suspend fun setPhoto(@Part file: MultipartBody.Part): SignedLinkResponse
}