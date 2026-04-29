package com.coursework.pleasantroutineui.domain

enum class TaskStatus(val displayName: String, val apiName: String) {
    OPEN("Открыто", "OPEN"),
    IN_PROGRESS("В работе", "IN_PROGRESS"),
    IN_REVIEW("На проверке", "IN_REVIEW"),
    DONE("Завершено", "DONE");

    companion object {
        fun fromApiName(api: String): TaskStatus =
            entries.first { it.apiName == api }

        fun fromDisplayName(display: String): TaskStatus =
            entries.first { it.displayName == display }
    }
}