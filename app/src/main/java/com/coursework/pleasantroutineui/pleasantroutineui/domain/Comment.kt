package com.coursework.pleasantroutineui.domain

import java.time.LocalDateTime


data class Comment(
    val id: Long,
    val text: String,
    val author: UserShort,
    val createdAt: String
)