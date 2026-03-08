package com.coursework.pleasantroutineui.repo.interfaces

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.coursework.pleasantroutineui.domain.Comment
import com.coursework.pleasantroutineui.domain.Task
import com.coursework.pleasantroutineui.domain.TaskStatus

interface ITaskRepo {

    fun getTask(id: String): Task

    fun getTasksByOwnerId(id: String): List<Task>

    fun changeTypeOfTask(id: String, newType: String)

    fun changeStatus(id: String, newStatus: String)

    fun getComments(id: String): List<Comment>
}