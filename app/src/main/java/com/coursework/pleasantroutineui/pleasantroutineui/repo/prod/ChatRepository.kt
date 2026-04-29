package com.coursework.pleasantroutineui.repo.prod

import com.coursework.pleasantroutineui.domain.ChatInfo
import com.coursework.pleasantroutineui.domain.ChatMessage
import com.coursework.pleasantroutineui.domain.CreateChatRequest
import com.coursework.pleasantroutineui.domain.Participant
import com.coursework.pleasantroutineui.services.ChatApiService
import com.coursework.pleasantroutineui.services.ChatWebSocketClient
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class ChatRepository @Inject constructor(
    private val api: ChatApiService,
    private val wsClient: ChatWebSocketClient
) {

    suspend fun getRoomChat(): ChatInfo? {
        val response = api.getRoomChat()
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun getParticipants(chatId: Long): List<Participant> {
        val response = api.getParticipants(chatId)
        return if (response.isSuccessful) response.body() ?: emptyList() else emptyList()
    }

    suspend fun getMessages(chatId: Long): List<ChatMessage> {
        val response = api.getMessages(chatId)
        return if (response.isSuccessful) response.body() ?: emptyList() else emptyList()
    }


    fun connectWebSocket(token: String) = wsClient.connect(token)
    fun joinChat(chatId: Long) = wsClient.joinChat(chatId)
    fun sendMessage(chatId: Long, text: String) = wsClient.sendMessage(chatId, text)
    fun disconnectWebSocket() = wsClient.disconnect()

    val incomingMessages = wsClient.incomingMessages
    val connectionState = wsClient.connectionState
}