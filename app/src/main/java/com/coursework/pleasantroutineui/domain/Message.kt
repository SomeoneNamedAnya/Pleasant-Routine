package com.coursework.pleasantroutineui.domain

data class Message(
    val id: String,
    val idChat: String,
    val text: String,
    val senderId: String,
    val timestamp: Long
)