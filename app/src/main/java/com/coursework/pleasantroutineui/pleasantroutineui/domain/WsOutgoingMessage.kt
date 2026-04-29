package com.coursework.pleasantroutineui.domain

data class WsOutgoingMessage(
    val chatId: Long,
    val text: String? = null
)