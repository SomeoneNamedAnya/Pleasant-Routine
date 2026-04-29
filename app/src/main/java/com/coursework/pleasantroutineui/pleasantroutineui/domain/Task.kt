package com.coursework.pleasantroutineui.domain

data class Task(
    val id: Long,
    val title: String,
    val description: String,
    val creationDate: String,
    val deadline: String,
    val type: TaskType,
    val status: TaskStatus,
    val roomId: Long,
    val creator: UserShort,
    val performers: List<UserShort>,
    val watchers: List<Watcher>
)