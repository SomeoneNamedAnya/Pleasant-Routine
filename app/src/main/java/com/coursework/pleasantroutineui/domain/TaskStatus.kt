package com.coursework.pleasantroutineui.domain

enum class TaskStatus(val displayName: String) {
    OPEN("Открыто"),
    IN_PROGRESS("В работе"),
    REVIEW("На проверке"),
    DONE("Завершено");

    companion object {
        fun fromDisplayName(name: String): TaskStatus {
            return values().find { it.displayName == name } ?: OPEN
        }
    }
}