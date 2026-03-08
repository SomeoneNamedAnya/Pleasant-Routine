package com.coursework.pleasantroutineui.domain

import java.time.LocalDateTime


data class Comment(
    val id: String,
    val text: String,
    val author: User,
    val date: LocalDateTime
)