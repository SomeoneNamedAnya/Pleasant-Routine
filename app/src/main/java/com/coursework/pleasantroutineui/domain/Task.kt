package com.coursework.pleasantroutineui.domain

data class Task(
    val id: String,
    val creationDate: String,
    val deadline: String,
    val watchers: List<User>,
    val creator: User,
    val performers: List<User>,
    val title: String,
    val description: String,
    val type: String,
    val status: TaskStatus,
    val roomId: String
)