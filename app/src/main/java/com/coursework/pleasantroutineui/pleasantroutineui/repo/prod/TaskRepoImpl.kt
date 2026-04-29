package com.coursework.pleasantroutineui.repo.prod

import com.coursework.pleasantroutineui.domain.Comment
import com.coursework.pleasantroutineui.domain.Task
import com.coursework.pleasantroutineui.domain.UserShort
import com.coursework.pleasantroutineui.domain.Watcher
import com.coursework.pleasantroutineui.dto.task.ApproveRequest
import com.coursework.pleasantroutineui.dto.task.ChangeStatusRequest
import com.coursework.pleasantroutineui.dto.task.CreateCommentRequest
import com.coursework.pleasantroutineui.dto.task.CreateTaskRequest
import com.coursework.pleasantroutineui.repo.interfaces.ITaskRepo
import com.coursework.pleasantroutineui.services.TaskApi
import retrofit2.HttpException
import javax.inject.Inject

class TaskRepoImpl @Inject constructor(
    private val api: TaskApi
) : ITaskRepo {

    override suspend fun getTask(id: Long): Task? = try {
        api.getTask(id).toDomain()
    } catch (e: Exception) { e.printStackTrace(); null }

    override suspend fun getMyTasks(): List<Task> = try {
        api.getMyTasks().map { it.toDomain() }
    } catch (e: Exception) { e.printStackTrace(); emptyList() }

    override suspend fun getTasksByRoom(roomId: Long): List<Task> = try {
        api.getTasksByRoom(roomId).map { it.toDomain() }
    } catch (e: Exception) { e.printStackTrace(); emptyList() }

    override suspend fun createTask(req: CreateTaskRequest): Task? = try {
        api.createTask(req).toDomain()
    } catch (e: Exception) { e.printStackTrace(); null }

    override suspend fun changeStatus(taskId: Long, req: ChangeStatusRequest): Result<Task> = try {
        Result.success(api.changeStatus(taskId, req).toDomain())
    } catch (e: HttpException) {
        Result.failure(Exception(e.response()?.errorBody()?.string() ?: "Ошибка"))
    } catch (e: Exception) { Result.failure(e) }

    override suspend fun getComments(taskId: Long): List<Comment> = try {
        api.getComments(taskId).map { it.toDomain() }
    } catch (e: Exception) { e.printStackTrace(); emptyList() }

    override suspend fun addComment(taskId: Long, req: CreateCommentRequest): Comment? = try {
        api.addComment(taskId, req).toDomain()
    } catch (e: Exception) { e.printStackTrace(); null }

    override suspend fun approve(taskId: Long, req: ApproveRequest): Result<Watcher> = try {
        Result.success(api.approve(taskId, req).toDomain())
    } catch (e: HttpException) {
        Result.failure(Exception(e.response()?.errorBody()?.string() ?: "Ошибка"))
    } catch (e: Exception) { Result.failure(e) }

    override suspend fun getMyRoomResidents(): List<UserShort> = try {
        api.getMyRoomResidents().map { it.toDomain() }
    } catch (e: Exception) {
        e.printStackTrace()
        emptyList()
    }
}