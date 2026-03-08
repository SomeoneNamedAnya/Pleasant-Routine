package com.coursework.pleasantroutineui.repo

import com.coursework.pleasantroutineui.domain.*
import com.coursework.pleasantroutineui.domain.TaskStatus.Companion.fromDisplayName
import com.coursework.pleasantroutineui.repo.interfaces.ITaskRepo
import java.time.LocalDateTime
import java.util.UUID

class TestTaskRepo : ITaskRepo {

    private val users = TestUsers()

    // 🔹 Список тестовых задач
    private val tasks = mutableListOf(

        Task(
            id = "0",
            roomId = "0",
            creationDate = "2026-02-10",
            deadline = "2026-03-01",
            creator = users.usersArr[0],
            performers = listOf(users.usersArr[1]),
            watchers = listOf(users.usersArr[2]),
            title = "Сделать экран регистрации",
            description = "Необходимо реализовать экран регистрации с валидацией email и пароля.",
            type = "Для каждого",
            status = TaskStatus.OPEN
        ),

        Task(
            id = "2",
            roomId = "0",
            creationDate = "2026-02-12",
            deadline = "2026-03-05",
            creator = users.usersArr[1],
            performers = listOf(users.usersArr[2]),
            watchers = listOf(users.usersArr[0]),
            title = "Подключить API задач",
            description = "Интегрировать Retrofit и настроить получение списка задач с сервера.",
            type = "Для каждого",
            status = TaskStatus.IN_PROGRESS
        ),

        Task(
            id = "3",
            roomId = "0",
            creationDate = "2026-02-14",
            deadline = "2026-03-07",
            creator = users.usersArr[2],
            performers = listOf(users.usersArr[0]),
            watchers = listOf(users.usersArr[1]),
            title = "Реализовать Kanban-доску",
            description = "Добавить экран канбан-доски с фильтрацией задач по статусам и переходом в TaskScreen.",
            type = "Хотя бы один",
            status = TaskStatus.REVIEW
        ),

        Task(
            id = "4",
            roomId = "0",
            creationDate = "2026-02-01",
            deadline = "2026-02-20",
            creator = users.usersArr[0],
            performers = listOf(users.usersArr[1], users.usersArr[2]),
            watchers = emptyList(),
            title = "Настроить MaterialTheme",
            description = "Настроить кастомную цветовую палитру приложения через MaterialTheme.colorScheme.",
            type = "Для каждого",
            status = TaskStatus.DONE
        )
    )

    // 🔹 Комментарии по taskId
    private val comments = mutableMapOf(
        "2" to mutableListOf(
            Comment(
                id = UUID.randomUUID().toString(),
                text = "Начал выполнять задачу",
                author = users.usersArr[2],
                date = LocalDateTime.now().minusHours(3)
            ),
            Comment(
                id = UUID.randomUUID().toString(),
                text = "Проверьте, пожалуйста",
                author = users.usersArr[1],
                date = LocalDateTime.now().minusMinutes(40)
            )
        )
    )

    // 🔹 Получение одной задачи
    override fun getTask(id: String): Task {
        println(id)
        return tasks.first { it.id == id }
    }

    // 🔹 Получение задач по id пользователя (например, по создателю)
    override fun getTasksByOwnerId(id: String): List<Task> {
        return tasks.filter { it.roomId == id }
    }

    // 🔹 Получение всех задач (если понадобится)
    fun getAllTasks(): List<Task> = tasks

    // 🔹 Получение комментариев
    override fun getComments(taskId: String): List<Comment> {
        return comments[taskId] ?: emptyList()
    }

    // 🔹 Изменение типа задачи
    override fun changeTypeOfTask(id: String, newType: String) {
        val index = tasks.indexOfFirst { it.id == id }
        if (index != -1) {
            tasks[index] = tasks[index].copy(type = newType)
        }
    }

    // 🔹 Изменение статуса задачи
    override fun changeStatus(id: String, newStatus: String) {
        val index = tasks.indexOfFirst { it.id == id }
        if (index != -1) {
            tasks[index] = tasks[index].copy(status = fromDisplayName(newStatus))
        }
    }
}