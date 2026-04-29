package com.coursework.pleasantroutineui.domain

data class ChatMessage(
    val id: Long,
    val chatId: Long,
    val senderId: Long,
    val senderName: String,
    val senderSurname: String,
    val senderPhotoLink: String?,
    val text: String,
    val timestamp: Long
)