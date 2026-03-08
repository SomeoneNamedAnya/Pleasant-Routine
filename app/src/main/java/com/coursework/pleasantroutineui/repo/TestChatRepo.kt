package com.coursework.pleasantroutineui.repo

import com.coursework.pleasantroutineui.domain.User
import com.coursework.pleasantroutineui.domain.Chat
import com.coursework.pleasantroutineui.domain.Message
import com.coursework.pleasantroutineui.repo.interfaces.IChatRepo

class TestChatRepo : IChatRepo {
    val users: TestUsers = TestUsers()

    private val chats = listOf(
        Chat(
            id = "1",
            title = "Чат комнаты C081",
            participants = users.usersArr.toList(),
            creationTime = System.currentTimeMillis()
        )
    )

    private val messagesStorage = mutableMapOf<String, MutableList<Message>>(
        "1" to mutableListOf(
            Message("1", "1", "Привет!", "2", System.currentTimeMillis()),
            Message("2", "1", "Здравствуйте!", "1", System.currentTimeMillis()),
            Message(
                "3",
                "1",
                "Это длинное тестовое сообщение для проверки переноса строк внутри bubble.",
                "0",
                System.currentTimeMillis()
            )
        )
    )

    override fun getChat(chatId: String): Chat {
        return chats.firstOrNull { it.id == chatId }
            ?: throw IllegalArgumentException("Chat with id=$chatId not found")
    }

    override fun getMessages(chatId: String): List<Message> {
        return messagesStorage[chatId]?.toList() ?: emptyList()
    }


    override fun sendMessage(message: Message) {
        val list = messagesStorage.getOrPut(message.idChat) { mutableListOf() }
        list.add(message)
    }

    override fun getParticipants(chatId: String): List<User> {
        return getChat(chatId).participants
    }
}