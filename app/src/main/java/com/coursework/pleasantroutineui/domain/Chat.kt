package com.coursework.pleasantroutineui.domain

data class Chat(
    val id: String,
    val title: String,
    val participants: List<User>,
    val creationTime: Long
)
