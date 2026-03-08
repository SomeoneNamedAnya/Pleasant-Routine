package com.coursework.pleasantroutineui.repo.interfaces

import com.coursework.pleasantroutineui.domain.Chat
import com.coursework.pleasantroutineui.domain.Message
import com.coursework.pleasantroutineui.domain.User

interface IChatRepo {

    fun getChat(chatId: String): Chat

    fun getMessages(chatId: String): List<Message>

    fun sendMessage(message: Message)

    fun getParticipants(chatId: String): List<User>
}