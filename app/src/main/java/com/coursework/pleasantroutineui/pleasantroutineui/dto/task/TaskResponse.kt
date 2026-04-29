package com.coursework.pleasantroutineui.dto.task

import com.coursework.pleasantroutineui.domain.Comment
import com.coursework.pleasantroutineui.domain.Task
import com.coursework.pleasantroutineui.domain.TaskStatus
import com.coursework.pleasantroutineui.domain.TaskType
import com.coursework.pleasantroutineui.domain.UserShort
import com.coursework.pleasantroutineui.domain.Watcher
import kotlinx.serialization.Serializable

@Serializable
data class TaskResponse(
    val id: Long,
    val title: String,
    val description: String,
    val createAt: String,
    val deadline: String,
    val type: String,
    val status: String,
    val roomId: Long,
    val creator: UserShortResponse?,
    val performers: List<UserShortResponse>,
    val watchers: List<WatcherResponse>
) {
    fun toDomain() = Task(
        id = id,
        title = title,
        description = description,
        creationDate = createAt,
        deadline = deadline,
        type = TaskType.fromApiName(type),
        status = TaskStatus.fromApiName(status),
        roomId = roomId,
        creator = creator?.toDomain() ?: UserShort(0, "?", "?", null),
        performers = performers.map { it.toDomain() },
        watchers = watchers.map { it.toDomain() }
    )
}

@Serializable
data class UserShortResponse(
    val id: Long,
    val name: String,
    val surname: String,
    val photoLink: String?
) {
    fun toDomain() = UserShort(id, name, surname, photoLink)
}

@Serializable
data class WatcherResponse(
    val id: Long,
    val name: String,
    val surname: String,
    val photoLink: String?,
    val approved: Boolean
) {
    fun toDomain() = Watcher(id, name, surname, photoLink, approved)
}

@Serializable
data class CommentResponse(
    val id: Long,
    val text: String,
    val author: UserShortResponse,
    val createdAt: String
) {
    fun toDomain() = Comment(id, text, author.toDomain(), createdAt)
}