package com.coursework.pleasantroutineui.services

import com.coursework.pleasantroutineui.domain.User
import com.coursework.pleasantroutineui.dto.LinkDto
import com.coursework.pleasantroutineui.dto.SignedLinkResponse
import com.coursework.pleasantroutineui.dto.user.AboutRequest
import com.coursework.pleasantroutineui.dto.user.UserRequest
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface UserApiService {

    @GET("/user/self_info")
    suspend fun getSelfInfo(): User

    @POST("/user/info")
    suspend fun getUserInfo(@Body request: UserRequest): User
    @Multipart
    @POST("/user/photo")
    suspend fun setPhoto(@Part file: MultipartBody.Part): SignedLinkResponse

    @POST("/user/about")
    suspend fun setAbout(@Body request: AboutRequest): Unit

    @POST("/link/sign")
    suspend fun signedLink(@Body request: LinkDto): SignedLinkResponse

}