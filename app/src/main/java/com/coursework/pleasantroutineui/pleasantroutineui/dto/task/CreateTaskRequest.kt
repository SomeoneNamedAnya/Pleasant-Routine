package com.coursework.pleasantroutineui.dto.task
import kotlinx.serialization.Serializable
@Serializable
data class CreateTaskRequest(
    val title: String,
    val description: String,
    val deadline: String,
    val type: String,
    val roomId: Long,
    val performerIds: List<Long>,
    val watcherIds: List<Long>

)

@Serializable
data class CreateCommentRequest(
    val text: String

)

@Serializable
data class ChangeStatusRequest(
    val newStatus: String

)

@Serializable
data class ApproveRequest(
    val approved: Boolean

)