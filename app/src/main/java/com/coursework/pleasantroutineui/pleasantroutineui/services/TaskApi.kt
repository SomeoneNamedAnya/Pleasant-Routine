package com.coursework.pleasantroutineui.services

import com.coursework.pleasantroutineui.dto.task.ApproveRequest
import com.coursework.pleasantroutineui.dto.task.ChangeStatusRequest
import com.coursework.pleasantroutineui.dto.task.CommentResponse
import com.coursework.pleasantroutineui.dto.task.CreateCommentRequest
import com.coursework.pleasantroutineui.dto.task.CreateTaskRequest
import com.coursework.pleasantroutineui.dto.task.TaskResponse
import com.coursework.pleasantroutineui.dto.task.UserShortResponse
import com.coursework.pleasantroutineui.dto.task.WatcherResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface TaskApi {

    @GET("api/tasks/{id}")
    suspend fun getTask(@Path("id") id: Long): TaskResponse

    @GET("api/tasks/my")
    suspend fun getMyTasks(): List<TaskResponse>

    @GET("api/tasks/room/{roomId}")
    suspend fun getTasksByRoom(@Path("roomId") roomId: Long): List<TaskResponse>

    @POST("api/tasks")
    suspend fun createTask(@Body req: CreateTaskRequest): TaskResponse

    @PUT("api/tasks/{id}/status")
    suspend fun changeStatus(
        @Path("id") id: Long,
        @Body req: ChangeStatusRequest
    ): TaskResponse

    @GET("api/tasks/{id}/comments")
    suspend fun getComments(@Path("id") id: Long): List<CommentResponse>

    @POST("api/tasks/{id}/comments")
    suspend fun addComment(
        @Path("id") id: Long,
        @Body req: CreateCommentRequest
    ): CommentResponse

    @POST("api/tasks/{id}/approve")
    suspend fun approve(
        @Path("id") id: Long,
        @Body req: ApproveRequest
    ): WatcherResponse

    @GET("api/tasks/{id}/approvals")
    suspend fun getApprovals(@Path("id") id: Long): List<WatcherResponse>

    @GET("api/tasks/my-room-residents")
    suspend fun getMyRoomResidents(): List<UserShortResponse>
}