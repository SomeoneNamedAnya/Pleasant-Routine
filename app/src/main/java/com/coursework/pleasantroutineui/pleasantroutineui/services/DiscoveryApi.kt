package com.coursework.pleasantroutineui.services

import com.coursework.pleasantroutineui.domain.discovery.CreateSharingRequest
import com.coursework.pleasantroutineui.domain.discovery.PagedNews
import com.coursework.pleasantroutineui.domain.discovery.PagedResponse
import com.coursework.pleasantroutineui.domain.discovery.RoomSearchResult
import com.coursework.pleasantroutineui.domain.discovery.UserSearchResult
import com.coursework.pleasantroutineui.domain.sharing.SharingCard
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface DiscoveryApi {


    @GET("api/discovery/people")
    suspend fun searchPeople(
        @Query("id") id: Long? = null,
        @Query("name") name: String? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): PagedResponse<UserSearchResult>

    @GET("api/discovery/rooms")
    suspend fun searchRooms(
        @Query("id") id: Long? = null,
        @Query("number") number: String? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): PagedResponse<RoomSearchResult>


    @GET("api/discovery/news")
    suspend fun getNews(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10
    ): PagedNews


    @POST("api/discovery/sharing")
    suspend fun createSharing(@Body body: CreateSharingRequest): SharingCard

    @POST("api/discovery/sharing/{id}/claim")
    suspend fun claimSharing(@Path("id") id: Long): SharingCard

    @GET("api/discovery/sharing/my-created")
    suspend fun myCreated(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10
    ): PagedResponse<SharingCard>

    @GET("api/discovery/sharing/my-claimed")
    suspend fun myClaimed(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10
    ): PagedResponse<SharingCard>

    @GET("api/discovery/sharing/all-active")
    suspend fun allActive(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10
    ): PagedResponse<SharingCard>

    @DELETE("api/discovery/sharing/{id}")
    suspend fun deleteSharing(@Path("id") id: Long)
}