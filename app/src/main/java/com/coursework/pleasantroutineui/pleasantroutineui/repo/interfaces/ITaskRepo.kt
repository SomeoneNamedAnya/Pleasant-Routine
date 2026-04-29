package com.coursework.pleasantroutineui.repo.interfaces

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.coursework.pleasantroutineui.domain.Comment
import com.coursework.pleasantroutineui.domain.Task
import com.coursework.pleasantroutineui.domain.TaskStatus
import com.coursework.pleasantroutineui.domain.UserShort
import com.coursework.pleasantroutineui.domain.Watcher
import com.coursework.pleasantroutineui.dto.task.ApproveRequest
import com.coursework.pleasantroutineui.dto.task.ChangeStatusRequest
import com.coursework.pleasantroutineui.dto.task.CreateCommentRequest
import com.coursework.pleasantroutineui.dto.task.CreateTaskRequest

interface ITaskRepo {
    suspend fun getTask(id: Long): Task?
    suspend fun getTasksByRoom(roomId: Long): List<Task>
    suspend fun createTask(req: CreateTaskRequest): Task?
    suspend fun changeStatus(taskId: Long, req: ChangeStatusRequest): Result<Task>
    suspend fun getComments(taskId: Long): List<Comment>
    suspend fun addComment(taskId: Long, req: CreateCommentRequest): Comment?
    suspend fun approve(taskId: Long, req: ApproveRequest): Result<Watcher>
    suspend fun getMyTasks(): List<Task>

    suspend fun getMyRoomResidents(): List<UserShort>
}