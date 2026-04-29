package com.coursework.pleasantroutineui.services

import com.coursework.pleasantroutineui.domain.ChatInfo
import com.coursework.pleasantroutineui.domain.ChatMessage
import com.coursework.pleasantroutineui.domain.CreateChatRequest
import com.coursework.pleasantroutineui.domain.Participant
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
interface ChatApiService {

    @GET("api/chat/room")
    suspend fun getRoomChat(): Response<ChatInfo>

    @GET("api/chat/{chatId}/participants")
    suspend fun getParticipants(@Path("chatId") chatId: Long): Response<List<Participant>>

    @GET("api/chat/{chatId}/messages")
    suspend fun getMessages(@Path("chatId") chatId: Long): Response<List<ChatMessage>>
}