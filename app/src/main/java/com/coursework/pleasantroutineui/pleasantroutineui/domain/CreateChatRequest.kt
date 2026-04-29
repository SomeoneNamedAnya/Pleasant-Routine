package com.coursework.pleasantroutineui.domain

data class CreateChatRequest(
    val title: String,
    val participantIds: List<Long>
)